package io.kool.mongodb

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBObject
import io.kool.stream.support.AbstractHandler
import java.util.ArrayList
import java.util.Collection
import java.util.HashMap
import java.util.List
import java.util.ListIterator
import java.util.Map
import java.util.concurrent.atomic.AtomicBoolean
import java.util.LinkedHashMap
import io.kool.collection.ObservableCollection
import io.kool.collection.CollectionEventListener
import io.kool.collection.support.CollectionEventPublisher

/**
* Returns the primary key of the given database object
*/
val DBObject.id: Any?
    get() = this["_id"]

/**
 * Represents an active collection which is kept to date using replication events
 */
public class ObservableDbCollection(val dbCollection: DBCollection, val query: DBObject? = null): ObservableCollection<DBObject> {
    val publisher = CollectionEventPublisher<DBObject>(this)

    private val updateLocalCollectionEagerly = false
    private var _idMap: Map<Any?, DBObject> = HashMap<Any?, DBObject>()

    val handler = ActiveDbCollectionHandler(this)
    var loaded = AtomicBoolean(false)

    public fun toString(): String = "ObservableDbCollection($dbCollection, $query)"

    public override fun equals(o: Any?): Boolean {
        return if (o is ObservableDbCollection) {
            this.dbCollection == o.dbCollection && this.query == o.query
        } else false
    }

    public override fun hashCode(): Int {
        var answer = 31 * dbCollection.hashCode()
        if (query != null) {
            answer += query.hashCode()
        }
        return answer;
    }

    public override fun addCollectionEventListener(listener: CollectionEventListener<DBObject>) {
        publisher.addCollectionEventListener(listener)
    }

    public override fun removeCollectionEventListener(listener: CollectionEventListener<DBObject>) {
        publisher.removeCollectionEventListener(listener)
    }

    protected val collection: Collection<DBObject>
        get() {
            return idMap.values()
        }

    protected val idMap: Map<Any?, DBObject>
        get() {
            checkLoaded()
            return _idMap
        }

    public fun isOpened(): Boolean = handler.isOpen()

    public fun isClosed(): Boolean = handler.isClosed()

    public fun onReplicaEvent(event: ReplicaEvent) {
        // we can discard events before we've loaded
        if (loaded.get()) {
            val change = event.change
            val id = change.get("_id")
            if (id != null) {
                sync {
                    val old = _idMap.get(id)
                    if (event.isDelete()) {
                        if (old != null) {
                            _idMap.remove(id)
                            publisher.fireRemoveEvent(old)
                        }
                    } else {
                        // TODO if we have a query defined we may need to
                        // decide if the change matches the query
                        // and if not, remove it if its no longer part of the view
                        // or include it if it does match
                        if (old != null) {
                            // lets process an update
                            val update = old.merge(change)
                            _idMap.put(id, update)
                            publisher.fireUpdateEvent(update)
                        } else {
                            _idMap.put(id, change)
                            publisher.fireAddEvent(change)
                        }
                    }
                }
            }
        }
    }

    protected fun checkLoaded() {
        if (loaded.compareAndSet(false, true)) {
            if (!handler.isOpen()) {
                val db = dbCollection.getDB()!!
                val dbName = db.getName()!!
                val collName = dbCollection.getName()!!
                val eventStream = db.getMongo()!!.replicationStream(databaseName = dbName, collectionName = collName, tail = true)
                eventStream.open(handler)
            }
            val cursor = if (query != null) {
                dbCollection.find(query)
            } else {
                dbCollection.find()
            }
            val newMap = LinkedHashMap<Any?, DBObject>()
            if (cursor != null) {
                for (e in cursor) {
                    val id = e.id
                    newMap[id] = e
                }
            }
            sync {
                _idMap = newMap
            }
        }
    }

    public fun flush() {
        sync {
            _idMap = HashMap<Any?, DBObject>()
            loaded.set(false)
        }
    }

    /**
     * Strategy function to perform synchronization around the _list and _idMap
     */
    protected fun <T> sync(block: () -> T): T {
        return synchronized (handler, block)
    }

    public fun get(key: Any?): DBObject? {
        return idMap.get(key)
    }

    // Collection API
    public override fun <R: Any?> toArray(a: Array<out R>): Array<R> {
        return collection.toArray(a)
    }

    public override fun toArray(): Array<Any?> = collection.toArray()

    public override fun add(element: DBObject): Boolean {
        println("Adding $element")
        val result = dbCollection.save(element)
        if (updateLocalCollectionEagerly) {
            val id = result?.getField("_id") ?: element.id
            val old = idMap.get(id)
            println("added id $id for $element and found old $old")
            idMap.put(id, element)
            if (old != null) {
                publisher.fireUpdateEvent(element)
            } else {
                publisher.fireAddEvent(element)
            }
            return old == null
        } else {
            return result?.getN() ?: 0 > 0
        }
    }

    public override fun addAll(c: Collection<out DBObject>): Boolean {
        var answer = false
        for (element in c) {
            answer = answer || add(element)
        }
        return answer
    }

    public override fun clear() {
        val removed = ArrayList<DBObject>()
        dbCollection.drop()
        flush()
        for (element in removed) {
            publisher.fireRemoveEvent(element)
        }
    }

    public override fun contains(element: Any?): Boolean {
        if (element is DBObject) {
            val id = element.id
            return idMap.containsKey(id)
        }
        return false;
    }

    public override fun containsAll(c: Collection<out Any?>): Boolean {
        for (e in c) {
            if (!contains(e)) return false
        }
        return true
    }

    public override fun isEmpty(): Boolean = idMap.isEmpty()

    public override fun iterator(): Iterator<DBObject> = collection.iterator()

    public override fun remove(element: Any?): Boolean {
        if (element is DBObject) {
            val result = dbCollection.remove(element)
            if (updateLocalCollectionEagerly) {
                val id = element.id
                val old = idMap.remove(id)
                if (old != null) {
                    publisher.fireRemoveEvent(element)
                    return true
                }
            } else {
                return result?.getN() ?: 0 > 0
            }
        }
        return false
    }

    public override fun removeAll(c: Collection<out Any?>): Boolean {
        var answer = false
        for (element in c) {
            answer = answer || remove(element)
        }
        return answer
    }

    public override fun retainAll(c: Collection<out Any?>): Boolean {
        throw UnsupportedOperationException()
    }

    public override fun size(): Int = idMap.size()
}


class ActiveDbCollectionHandler(val activeCollection: ObservableDbCollection): AbstractHandler<ReplicaEvent>() {

    public override fun toString(): String = "ActiveDbCollectionHandler($activeCollection)"

    public override fun onNext(next: ReplicaEvent) {
        activeCollection.onReplicaEvent(next)
    }

}