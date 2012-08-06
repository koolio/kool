package test.kool.mongodb

import io.kool.mongodb.*
import io.kool.stream.MockHandler
import org.junit.Test as test
import io.kool.stream.Cursor

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

        val mock1 = MockHandler<ReplicaEvent>().expectReceive(1)
        val cursor1 = stream.open(mock1)
        mock1.assertWaitForOpen()

        // now lets insert some data to force events to be raised
        val o = dbObject("name" to "James", "location" to "Mells")
        println("Inserting object $o")
        val result = collection.insert(o)

        println("Now waiting for tail notifications....")
        mock1.assertExpectations()
        cursor1.close()
        mock1.assertWaitForClose()


        // now we should be able to process the stream without tailing from the beginning
        val nonTailStream = mongo.replicationStream() filter {
            it.databaseName == testDbName && it.collectionName == testCollectionName
        } forEach {
            println("Got head replication: ${it.json}")
        }

        val mock2 = MockHandler<ReplicaEvent>().expectReceive(1)
        val cursor2 = nonTailStream.open(mock2)

        println("Now waiting for head notifications....")
        mock2.assertExpectations()

        println("Asserts worked!")
        println("mock1 expecations: ${mock1.expectations}")
        println("mock2 expecations: ${mock2.expectations}")
        println("mock1 events: ${mock1.events}")
        println("mock2 events: ${mock2.events}")

        cursor2.close()
        mock2.assertWaitForClose()
    }
}
