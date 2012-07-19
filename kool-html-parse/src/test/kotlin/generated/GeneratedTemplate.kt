package dummyPackage

import kotlin.dom.*
import io.kool.template.html.*
import org.w3c.dom.*

public fun myTemplate(doc: Document = createDocument()): Element = doc.html {
    head {
        title("This is my title")
    }
    body {
        h1("My H1 Title")
        p("Some text")
        a(text = "this is a hyperlink", href = "http://kool.io/", title = "link title")
        ul {
            li("one")
            li("two")
            li("three")
        }
    }
}
