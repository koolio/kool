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

/**
* Returns the primary key of the given database object
*/
val DBObject.id: Any?
    get() = this["_id"]

/**
 * Represents an active collection which is kept to date using replication events
 */
public class ActiveDbCollection(val dbCollection: DBCollection, val query: DBObject? = null): List<DBObject> {
    // TODO do we really need both the List and the Map?
    private var _list: List<DBObject> = ArrayList<DBObject>()
    private var _idMap: Map<Any?, DBObject> = HashMap<Any?, DBObject>()

    val handler = ActiveDbCollectionHandler(this)
    var loaded = AtomicBoolean(false)

    public override fun toString(): String = "ActiveDbCollection($dbCollection, $query)"

    public override fun equals(o: Any?): Boolean {
        return if (o is ActiveDbCollection) {
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

    protected val list: List<DBObject>
        get() {
            checkLoaded()
            return _list
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
        println("Got a change event $event")
        if (loaded.get()) {
            val change = event.change
            val id = change.get("_id")
            if (id != null) {
                sync {
                    val old = _idMap.get(id)
                    if (event.isDelete()) {
                        if (old != null) {
                            _idMap.remove(id)
                            _list.remove(old)
                        }
                    } else {
                        if (old != null) {
                            // lets process an update
                            val newValue = old.merge(change)
                            _idMap.put(id, newValue)
                            _list.remove(old)
                            _list.add(newValue)
                        } else {
                            _idMap.put(id, change)
                            _list.add(change)
                        }
                    }
                }
            }
        }
    }

    protected fun checkLoaded() {
        if (loaded.compareAndSet(false, true)) {
            val cursor = if (query != null) {
                dbCollection.find(query)
            } else {
                dbCollection.find()
            }
            val newMap = LinkedHashMap<Any?, DBObject>()
            val newList = if (cursor != null) {
                for (e in cursor) {
                    val id = e.id
                    newMap[id] = e
                }
                ArrayList<DBObject>(newMap.values())
            } else {
                ArrayList<DBObject>()
            }
            sync {
                _list = newList
                _idMap = newMap
            }
        }
    }

    public fun flush() {
        sync {
            _list = ArrayList<DBObject>()
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

    // List API
    public override fun <T: Any?> toArray(a: Array<T>): Array<T> = list.toArray(a)

    public override fun toArray(): Array<Any?> = list.toArray()

    public override fun add(element: DBObject): Boolean {
        var answer = false
        val result = dbCollection.save(element)
        val id = result?.getField("_id") ?: element.id
        val old = idMap.get(id)
        if (old != null) {
            list.remove(old)
        } else {
            answer = true
        }
        list.add(element)
        idMap.put(id, element)
        return answer
    }

    public override fun add(index: Int, element: DBObject) {
        add(element)
    }

    public override fun addAll(c: Collection<out DBObject>): Boolean {
        var answer = false
        for (element in c) {
            answer = answer || add(element)
        }
        return answer
    }

    public override fun addAll(index: Int, c: Collection<out DBObject>): Boolean {
        return addAll(c)
    }

    public override fun clear() {
        dbCollection.drop()
        flush()
    }
    public override fun contains(o: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    public override fun containsAll(c: Collection<out Any?>): Boolean {
        throw UnsupportedOperationException()
    }

    public override fun get(index: Int): DBObject {
        return list.get(index)
    }

    public override fun indexOf(o: Any?): Int {
        throw UnsupportedOperationException()
    }

    public override fun isEmpty(): Boolean = list.isEmpty()

    public override fun iterator(): java.util.Iterator<DBObject> = list.iterator()

    public override fun lastIndexOf(o: Any?): Int {
        throw UnsupportedOperationException()
    }
    public override fun listIterator(): ListIterator<DBObject> = list.listIterator()

    public override fun listIterator(index: Int): ListIterator<DBObject> = list.listIterator(index)

    public override fun remove(index: Int): DBObject {
        val answer = list.remove(index)
        if (answer != null) {
            dbCollection.remove(answer)
        }
        return answer
    }

    public override fun remove(element: Any?): Boolean {
        if (element is DBObject) {
            val id = element.id
            val old = idMap.remove(id)
            if (old != null) {
                dbCollection.remove(element)
                list.remove(element)
                return true
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

    public override fun set(index: Int, element: DBObject): DBObject {
        throw UnsupportedOperationException()
    }

    public override fun size(): Int = list.size()

    public override fun subList(fromIndex: Int, toIndex: Int): List<DBObject> = list.subList(fromIndex, toIndex)
}


class ActiveDbCollectionHandler(val activeCollection: ActiveDbCollection): AbstractHandler<ReplicaEvent>() {

    public override fun toString(): String = "ActiveDbCollectionHandler($activeCollection)"

    public override fun onNext(next: ReplicaEvent) {
        activeCollection.onReplicaEvent(next)
    }

}