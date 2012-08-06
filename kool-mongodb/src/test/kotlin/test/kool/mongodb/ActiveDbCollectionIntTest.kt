package test.kool.mongodb

import io.kool.mongodb.*
import io.kool.stream.MockHandler
import org.junit.Test as test
import io.kool.stream.Cursor
import kotlin.test.*

class ActiveDbCollectionIntTest: MongoTestSupport() {
    val testCollectionName = "activeDbCollectionTest"

    test fun stream() {
        // clear the test dbCollection
        val dbCollection = db.getCollection(testCollectionName)!!
        dbCollection.drop()

        val activeCollection = db.activeCollection(testCollectionName)
        assertEquals(0, activeCollection.size())

        // now lets insert into the dbCollection
        val location1 = dbObject("city" to "Mells")
        val person1 = dbObject("name" to "James", "location1" to location1)
        dbCollection.insert(person1)

        waitForActiveChange()

        println("Active dbCollection ${activeCollection.makeString(", ", "[", "]")}")

        // now the active dbCollection should contain some values
        assertEquals(1, activeCollection.size())

        // now lets update the value
        person1.put("name", "James2")
        location1.put("country", "UK")
        dbCollection.save(person1)

        waitForActiveChange()

        assertEquals(1, activeCollection.size())

        // now lets insert directly into the database and active collection
        val person2 = dbObject("name" to "Hiram")
        activeCollection.add(person2)
        assertEquals(2, activeCollection.size())

        // lets check that the underlying collection has changed
        val list = dbCollection.find().iterator().toList()
        assertEquals(2, list.size(), "dbCollection was $list")

        waitForActiveChange()
        assertEquals(2, activeCollection.size())

        println("ActiveDbCollection after inserts ${activeCollection.makeString(", ", "[", "]")}")

        // now lets remove from the underlying dbCollection
        dbCollection.remove(person2)

        waitForActiveChange()
        println("ActiveDbCollection after remove ${activeCollection.makeString(", ", "[", "]")}")
        assertEquals(1, activeCollection.size())
    }

    fun waitForActiveChange() {
        // TODO should have a better way to do this using event listeners :)
        Thread.sleep(5000)

    }
}
