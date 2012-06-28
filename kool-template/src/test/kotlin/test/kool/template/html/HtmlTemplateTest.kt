package test.kool.template.html

import io.kool.template.html.*
import org.w3c.dom.*

import kotlin.dom.*

import org.junit.Test

class HtmlTemplateTest {
    Test fun createDom() {
        val document = createDocument()
        val dom = document.html {
            body {
                h1("Hey")
                h2 {
                    text = "my title"
                }
                p {
                    a(href = "hey", text = "link text")

                    this + "some text"

                    a(href = "cheese.html", title = "my link title") {
                        img(src = "blah.jpg")
                    }
                }
            }
        }
        println("HTML is ${dom.toXmlString()}")
    }
}