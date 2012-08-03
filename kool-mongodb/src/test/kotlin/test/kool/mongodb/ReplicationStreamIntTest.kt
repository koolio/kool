package test.kool.mongodb

import io.kool.mongodb.*
import com.mongodb.Mongo

import org.junit.Test as test
import kotlin.test.*

/**
 * Tests using a replication stream
 */
class ReplicationStreamIntTest : MongoTestSupport() {
    val testCollectionName = "replicationStreamTest"

    test fun stream() {
        // clear the test collection
        val collection = db.getCollection(testCollectionName)!!
        collection.drop()

        // create a replication stream
        val stream = mongo.replicationStream(tail = true).filter { it.databaseName == testDbName && it.collectionName == testCollectionName }
        stream.open {
            println("Got tail replication: ${it.json}")
        }

        // now lets insert some data to force events to be raised

        // lets wait for some objects to be written
        Thread.sleep(2000)

        val o = dbObject("name" to "James", "location" to "Mells")
        println("Inserting object $o")
        val result = collection.insert(o)
        println("result $result")

        println("Now waiting for tail notifications....")
        Thread.sleep(10000)



        // now we should be able to process the stream without tailing from the beginning
        val nonTailStream = mongo.replicationStream().filter { it.databaseName == testDbName && it.collectionName == testCollectionName }.take(1)
        nonTailStream.open {
            println("Got head replication: ${it.json}")
        }

        println("Now waiting for head notifications....")
        Thread.sleep(10000)
    }

}