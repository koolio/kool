package test.kool.mongodb

import org.junit.Test as test
import org.codehaus.jackson.map.ObjectMapper
import kotlin.test.assertEquals
import org.bson.types.ObjectId

class PersonMarshalTest {
    test fun marshal() {

        //val p = Person("James", "Mells")
        val p = Person.create("James", "Mells")
        p.id = ObjectId().toString()

        val mapper = ObjectMapper()
        val json = mapper.writeValueAsString(p)

        println("person json: $json")

        val actual = mapper.readValue<Person>(json, javaClass<Person>())!!
        assertEquals("James", actual.name)
        assertEquals("Mells", actual.location)
    }
}