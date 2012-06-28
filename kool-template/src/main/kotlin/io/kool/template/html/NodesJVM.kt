package io.kool.template.html

import java.io.Writer
import kotlin.dom.*
import org.w3c.dom.*

/**
* Renders the node as XML markup
*/
fun Node.render(out: Appendable): Unit {
    if (out is Writer) {
        writeXmlString(out, false)
    } else {
        out.append(toXmlString())
    }
}