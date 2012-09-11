package test.kool.mongodb

import kotlin.test.*
import net.vz.mongodb.jackson.JacksonDBCollection
import net.vz.mongodb.jackson.internal.MongoJacksonMapperModule
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test as test

class PersonPersistIntTest : MongoTestSupport() {
    test fun persist() {
        val objectMapper = ObjectMapper()
        MongoJacksonMapperModule.configure(objectMapper)

        val people = JacksonDBCollection.wrap<Person, String>(db.getCollection("people"), javaClass<Person>(), javaClass<String>(), objectMapper)!!

        // lets empty the collection first
        people.drop()

        var first = people.findOne()
        println("Found $first")
        if (first == null) {
            val p = Person.create("James", "Mells")
            val r = people.insert(p)
            if (r != null) {
                // TODO would be nice to automate this!
                // lets populate the new ID -
                // TODO this gets a conversion issue...
                //p.id = r.getSavedId()
                p.id = r.getDbObject()?.get("_id").toString()
            }
            println("Inserted $p")
            first = p
        }
        assertNotNull(first)
        val id = first?.id
        val actual = people.findOneById(id)
        println("findOneById($id) found: $actual")

        assertNotNull(actual)
        assertEquals(first?.name, actual?.name)
        assertEquals(first?.location, actual?.location)
    }
}