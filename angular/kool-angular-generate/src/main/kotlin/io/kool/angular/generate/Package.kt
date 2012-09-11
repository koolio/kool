package io.kool.angular.generate

import org.w3c.dom.Element

val StringDefinition = PrimitiveDefinition("String")
val FloatDefinition = PrimitiveDefinition("Float")

fun main(args: Array<String>): Unit {
    val generator = ModelGenerator(args)
    generator.run()
}

/**
 * Returns the given ng attribute value
 * or tries "data-" + ngName if its not defined
 */
fun Element.ngAttribute(name: String): String? {
    val answer = getAttribute(name)
    return if (answer.notEmpty()) {
        answer
    } else {
        getAttribute("data-" + name)
    }
}