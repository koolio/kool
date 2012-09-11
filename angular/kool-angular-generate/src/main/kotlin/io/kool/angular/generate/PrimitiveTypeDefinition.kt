package io.kool.angular.generate


import java.util.HashMap

/**
 * A primitive type
 */
public class PrimitiveDefinition(override val name: String): TypeDefinition {
    override val primitive: Boolean = true

    fun toString(): String = name
}