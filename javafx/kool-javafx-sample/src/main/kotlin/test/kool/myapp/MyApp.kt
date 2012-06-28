package test.kool.myapp

import io.kool.template.html.*
import org.w3c.dom.Document
import org.w3c.dom.Node
import kotlin.browser.*
import kotlin.dom.*

/**
 * Entry point to my application which can be called
 * from a JS browser when its ready, or from JavaFX
 */
fun myApp() {
    val newNode = sampleTemplate(document)
    println("About to insert DOM node $newNode into document $document")

    val container = document.getElementById("view")
    if (container != null) {
        container.appendChild(newNode)
        println("Added new node!!!")
    } else println("Cannot find container!")
}

fun sampleTemplate(document: Document): Node {
    return document.div {
        h2("Kool Template Generated Content!") {}
    }
}