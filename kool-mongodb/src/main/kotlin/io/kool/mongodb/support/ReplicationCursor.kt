package io.kool.mongodb.support

import com.mongodb.BasicDBObject
import com.mongodb.Bytes
import io.kool.mongodb.ReplicaEvent
import io.kool.stream.Handler
import io.kool.stream.support.AbstractCursor
import org.bson.types.BSONTimestamp

class ReplicationCursor(val stream: ReplicationStream, val handler: Handler<ReplicaEvent>): AbstractCursor(), Runnable {
    var timestamp = stream.timestamp
    val oplog = stream.oplog

    public override fun toString(): String? = "ReplicationCursor(collection: ${stream.oplog} handler: $handler timestamp: ${stream.timestamp})"

    public override fun run() {
        //println("Starting handler $handler on $this")
        handler.onOpen(this)
        try {
            if (timestamp == null && stream.tail) {
                // lets load the last timestamp from the collection
                val cursor = oplog.find()?.sort(BasicDBObject("\$natural", -1))?.limit(1)!!
                if (cursor.hasNext()) {
                    val last = cursor.next()
                    if (last != null) {
                        val entry = ReplicaEvent(last)
                        timestamp = entry.timestamp
                        //println("$this starting from timestamp $timestamp from entry $entry")
                    }
                }
            }
            if (timestamp == null) {
                // lets define a very small timestamp
                timestamp = BSONTimestamp(0, 1)
            }
            while (!isClosed()) {
                val query = BasicDBObject("ts", BasicDBObject("\$gt", timestamp))
                val dbName = stream.databaseName
                val collName = stream.collectionName
                if (dbName != null && collName != null) {
                    val ns = dbName + "." + collName
                    query.put("ns", ns)
                } else if (dbName != null) {
                    query.put("ns", "/$dbName\\..*/i")
                } else if (collName != null) {
                    query.put("ns", "/.*\\.$collName/i")
                }
                val cursor = oplog.find(query)!!
                cursor.addOption(Bytes.QUERYOPTION_TAILABLE)
                cursor.addOption(Bytes.QUERYOPTION_AWAITDATA)

                while (!isClosed() && cursor.hasNext()) {
                    val x = cursor.next()
                    if (x != null) {
                        val entry = ReplicaEvent(x)
                        timestamp = entry.timestamp
                        handler.onNext(entry)
                    }
                }
            }
            handler.onComplete()
        } catch (e: Throwable) {
            handler.onError(e)
            throw e
        }
    }

    protected override fun doClose() {
    }

}