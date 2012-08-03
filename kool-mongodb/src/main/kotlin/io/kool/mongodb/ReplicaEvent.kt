package io.kool.mongodb

import com.mongodb.DBObject
import org.bson.types.BSONTimestamp

/**
 * Represents an event in the MongoDb [Replica Set](http://www.mongodb.org/display/DOCS/Replica+Set+Tutorial)
 * which is usually the **oplog.rs** collection in the **local** database
 * which is used to replicate changes to slave databases
 */
public class ReplicaEvent(val dbObject: DBObject) {
    private var _timestamp: BSONTimestamp? = null
    private var _change: DBObject? = null
    private var _operation: String? = null
    private var _namespace: String? = null
    private var _databaseName: String? = null
    private var _collectionName: String? = null
    private var _id: Long? = null

    fun toString(): String = "ReplicaEvent(database: $databaseName collection: $collectionName ts: $timestamp, id $id, op: $operation, change: $change)"

    /**
     * Returns the JSON representation of the entry
     */
    public val json: String = dbObject.toString() ?: "{}"

    /**
     * Returns the timestamp of the change
     */
    public val timestamp: BSONTimestamp?
    get() {
        if (_timestamp == null) {
            _timestamp = dbObject.get("ts") as BSONTimestamp
        }
        return _timestamp
    }

    /**
     * Returns the unique ID of the change
     */
    public val id: Long?
    get() {
        if (_id == null) {
            _id = dbObject.get("h") as Long
        }
        return _id
    }

    /**
     * Returns the underlying database change (insert, update or delete data)
     */
    public val change: DBObject
    get() {
        if (_change == null) {
            _change = dbObject.get("o") as DBObject
        }
        return _change!!
    }

    /**
     * Returns the database namespace of the form database.collection
     */
    public val namespace: String
    get() {
        if (_namespace == null) {
            val value = dbObject.get("ns") as String
            _namespace = value
            if (value != null) {
                val array = value.split('.')
                if (array.size > 0) {
                    _databaseName = array[0]
                    if (array.size > 1) {
                        _collectionName = array[1]
                    }
                }
            }
        }
        return _namespace!!
    }

    /**
     * Returns the database name
     */
    public val databaseName: String
    get() {
        // force lazy construction
        namespace
        return _databaseName!!
    }

    /**
     * Returns the collection name
     */
    public val collectionName: String
    get() {
        // force lazy construction
        namespace
        return _collectionName!!
    }

    /**
     * Returns the database operation, either "i", "u", "d" for insert/update/delete
     */
    public val operation: String
    get() {
        if (_operation == null) {
            _operation = dbObject.get("op") as String
        }
        return _operation!!
    }

    /**
     * Returns true if this operation is an insert
     */
    public fun isInsert(): Boolean = "i" == operation

    /**
     * Returns true if this operation is an update
     */
    public fun isUpdate(): Boolean = "u" == operation

    /**
     * Returns true if this operation is a delete
     */
    public fun isDelete(): Boolean = "d" == operation
}