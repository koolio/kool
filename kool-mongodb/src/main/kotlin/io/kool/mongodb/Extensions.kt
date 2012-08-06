package io.kool.mongodb

import com.mongodb.DBCollection
import io.kool.stream.Stream
import com.mongodb.DB
import com.mongodb.Mongo
import org.bson.types.BSONTimestamp
import com.mongodb.DBObject
import com.mongodb.BasicDBObject
import io.kool.mongodb.support.ReplicationStream

/**
 * Creates a [[Stream<ReplicationEntry>]] from the given Mongo.
 */
public fun Mongo.replicationStream(val timestamp: BSONTimestamp? = null, val tail: Boolean = false): Stream<ReplicaEvent> {
    return getDB("local")!!.replicationStream(timestamp, tail)
}

/**
 * Creates a [[Stream<ReplicationEntry>]] from the given database.
 */
public fun DB.replicationStream(val timestamp: BSONTimestamp? = null, val tail: Boolean = false): Stream<ReplicaEvent> {
    return getCollection("oplog.rs")!!.replicationStream(timestamp, tail)
}

/**
 * Creates a [[Stream<ReplicationEntry>]] from the given collection.
 */
public fun DBCollection.replicationStream(val timestamp: BSONTimestamp? = null, val tail: Boolean = false): Stream<ReplicaEvent> {
    return ReplicationStream(this, timestamp, tail)
}

/**
 * Helper method to create a [[DBObject]] using either tuple notation or using
 * the 'to' infix operator.
 */
public inline fun dbObject(vararg values: #(String,Any?)): DBObject {
    val answer = BasicDBObject()
    for (v in values) {
        answer.put(v._1, v._2)
    }
    return answer
}
