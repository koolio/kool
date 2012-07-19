package io.kool.template.html

import kotlin.dom.*
import org.w3c.dom.*

fun Node.element(localName: String, init: Element.()-> Unit): Element {
    val element = ownerDocument().createElement(localName).sure()
    element.init()
    if (this.nodeType == Node.ELEMENT_NODE) {
        appendChild(element)
    }
    return element
}

fun Node.textElement(localName: String, text: String? = null, init: Element.()-> Unit): Element {
    val answer = element(localName, init)
    if (text != null) answer.addText(text)
    return answer
}

/**
 * Helper method to add some text to an element
 */
fun Element.text(text: String): Element {
    addText(text)
    return this
}
