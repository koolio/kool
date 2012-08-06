package test.kool.mongodb

import io.kool.mongodb.*
import io.kool.stream.MockHandler
import org.junit.Test as test
import io.kool.stream.Cursor
import kotlin.test.*

class ActiveDbCollectionIntTest: MongoTestSupport() {
    val testCollectionName = "activeDbCollectionTest"

    test fun stream() {
        // clear the test collection
        val collection = db.getCollection(testCollectionName)!!
        collection.drop()

        val activeCollection = db.activeCollection(testCollectionName)

        assertEquals(0, activeCollection.size())
        // now lets insert into the collection
        collection.insert( dbObject("name" to "James", "city" to "Mells"))

        println("current collection ${collection.find().iterator().makeString(", ", "[", "]")}")


        Thread.sleep(5000)

        println("Active collection ${activeCollection.makeString(", ", "[", "]")}")

        // now the active collection should contain some values
        assertEquals(1, activeCollection.size())

        // now lets update the value



        for (e in activeCollection) {
            println("Element: $e")
        }
    }
}
