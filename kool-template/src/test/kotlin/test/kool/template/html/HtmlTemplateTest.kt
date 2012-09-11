package test.kool.template.html

import io.kool.template.html.*
import kotlin.dom.*
import kotlin.test.*
import org.junit.Test as test

class HtmlTemplateTest {
    test fun createDom() {
        val document = createDocument()
        val dom = document.html {
            body {
                h1("Hey")
                h2 {
                    text = "my title"
                }
                p {
                    a(href = "foo.html", text = "link text")

                    this + "some text"

                    a(href = "cheese.html", title = "my link title") {
                        img(src = "blah.jpg")
                    }
                }
            }
        }
        println("HTML is ${dom.toXmlString()}")
        val links = dom["a"]
        val a1 = links[0]!!
        println("first a ${a1.toXmlString()}")
        //assertEquals("foo.html", a1["@href"])
        assertEquals("foo.html", a1.getAttribute("href"))
        assertEquals("link text", a1.text)

        val a2 = links[1]!!
        println("second a ${a2.toXmlString()}")
        //assertEquals("cheese.html", a2["@href"])
        assertEquals("cheese.html", a2.getAttribute("href"))
    }
}