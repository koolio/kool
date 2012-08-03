package test.kool.mongodb.watcher

import com.mongodb.*
import org.junit.Test as test
import kotlin.test.*
import io.kool.mongodb.ReplicaEvent
import test.kool.mongodb.MongoTestSupport

class DatabaseWatcher : MongoTestSupport() {
    test fun watchChanges() {
        val db = mongo.getDB("local")!!

        val oplog = db.getCollection("oplog.rs")!!

        val lastCursor = oplog.find()?.sort(BasicDBObject("\$natural", -1))?.limit(1)!!
        assertTrue(lastCursor.hasNext(), "Should have a cursor!")

        val last = lastCursor.next()!!
        val lastEntry = ReplicaEvent(last)
        var ts = lastEntry.timestamp
        println("Starting at time $ts with $lastEntry")


        while (true) {
            val cursor = oplog.find(BasicDBObject("ts",BasicDBObject("\$gt", ts)))!!
            cursor.addOption(Bytes.QUERYOPTION_TAILABLE)
           	cursor.addOption(Bytes.QUERYOPTION_AWAITDATA)

            while (cursor.hasNext()) {
                val x = cursor.next()
                if (x != null) {
                    val entry = ReplicaEvent(x)
                    ts = entry.timestamp
                    println("$entry")
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    DatabaseWatcher().watchChanges()
}