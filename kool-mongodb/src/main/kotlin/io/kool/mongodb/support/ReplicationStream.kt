package io.kool.mongodb

import com.mongodb.DBCollection
import io.kool.stream.Cursor
import io.kool.stream.Handler
import io.kool.stream.Stream
import org.bson.types.BSONTimestamp

/**
* A [[Stream]] of [[ReplicationEntry]] events
*/
public class ReplicationStream(val oplog: DBCollection, val timestamp: BSONTimestamp?, val tail: Boolean): Stream<ReplicaEvent>() {
    override fun open(handler: Handler<ReplicaEvent>): Cursor {
        val cursor = ReplicationCursor(this, handler)
        val thread = Thread(cursor, cursor.toString())
        thread.start()
        return cursor
    }
}
