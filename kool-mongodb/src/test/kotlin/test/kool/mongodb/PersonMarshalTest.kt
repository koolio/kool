package test.kool.mongodb

import kotlin.test.assertEquals
import org.bson.types.ObjectId
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test as test

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