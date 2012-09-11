package io.kool.angular.generate

/**
* A primitive type
*/
public class PrimitiveDefinition(override val name: String): TypeDefinition {
    override val primitive: Boolean = true

    fun toString(): String = name
}