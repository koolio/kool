package io.kool.angular.generate

import java.util.HashMap

/**
* Represents the metadata about the model we can extract from the
*/
public class ClassDefinition(override val name: String): TypeDefinition {
    val members: Map<String, TypeDefinition> = HashMap<String, TypeDefinition>()

    override val primitive: Boolean = false

    fun toString(): String = "class $name {${members.map{ "${it.key}: ${it.value}" }.makeString(", ")}}"

    fun classDefinition(name: String): ClassDefinition {
        val answer = members.getOrPut(name) { ClassDefinition(name) }
        require(answer is ClassDefinition) { "value $answer is not a ClassDefinition" }
        return answer as ClassDefinition
    }

}