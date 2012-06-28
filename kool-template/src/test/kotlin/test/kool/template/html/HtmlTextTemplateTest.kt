package test.kool.template.html

import io.kool.template.html.*
import org.w3c.dom.*

import kotlin.dom.*

import org.junit.Test

class HtmlTextTemplateTest {
    Test fun createDom() {
        val document = createDocument()
        val dom = document.html {
            body {
                h1("Hey")
                h1 {
                    // TODO
                    // ideally this would work
                    // +"my title"
                    this + "my title"
                }
            }
        }
        println("Xml is ${dom.toXmlString()}")
    }
}