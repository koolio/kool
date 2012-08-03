package test.kool.mongodb

import io.kool.mongodb.*
import io.kool.stream.MockHandler
import org.junit.Test as test

/**
* Tests using a replication stream
*/
class ReplicationStreamIntTest: MongoTestSupport() {
    val testCollectionName = "replicationStreamTest"

    test fun stream() {
        // clear the test collection
        val collection = db.getCollection(testCollectionName)!!
        collection.drop()

        // create a replication stream
        val stream = mongo.replicationStream(tail = true) filter {
            it.databaseName == testDbName && it.collectionName == testCollectionName
        } forEach {
            println("Got tail replication: ${it.json}")
        }

        val mock1 = MockHandler<ReplicaEvent>()
        mock1.expectReceive(1)
        stream.open(mock1)

        // TODO: lets wait for the handler to be opened
        Thread.sleep(1000)

        // now lets insert some data to force events to be raised
        val o = dbObject("name" to "James", "location" to "Mells")
        println("Inserting object $o")
        val result = collection.insert(o)

        println("Now waiting for tail notifications....")
        mock1.assertExpectations()


        // now we should be able to process the stream without tailing from the beginning
        val nonTailStream = mongo.replicationStream() filter {
            it.databaseName == testDbName && it.collectionName == testCollectionName
        } forEach {
            println("Got head replication: ${it.json}")
        }

        val mock2 = MockHandler<ReplicaEvent>()
        mock2.expectReceive(1)
        nonTailStream.open(mock2)

        println("Now waiting for head notifications....")
        mock2.assertExpectations()
    }
}
