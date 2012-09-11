package test.kool.mongodb

import net.vz.mongodb.jackson.ObjectId
import org.codehaus.jackson.annotate.JsonProperty

//import org.bson.types.ObjectId
/*
 TODO when this issue is fixed: http://youtrack.jetbrains.com/issue/KT-2543
 it would be good to be able to use immutable data transfer objects in kotlin

class Person [JsonCreator] (
        JsonProperty("name") val name: String,
        JsonProperty("location") val location: String,
        JsonProperty("id") val id: ObjectId? = null) {
    public fun toString(): String = "Person($id, $name, $location)"
}
*/

class Person {
    public var name: String? = null

    public var location: String? = null

    public JsonProperty("_id") ObjectId var id: String? = null
    //public Id var id: String? = null

    public fun toString(): String = "Person($id, $name, $location)"

    class object {
        fun create(name: String, location: String): Person {
            val answer = Person()
            answer.name = name
            answer.location = location
            return answer
        }
    }
}