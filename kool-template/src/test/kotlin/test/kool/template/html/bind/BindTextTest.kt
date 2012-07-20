package test.kool.template.html.bind

import io.kool.template.html.*
import org.w3c.dom.*
import kotlin.dom.*

import org.junit.Test as test
import kotlin.test.*

class BindTextTest {
    test fun refreshBinding() {
        val person = Person("name1")

        val bind = Binder()

        val document = html {
            body {
                h1("Hey")

                h2 {
                    bind {
                        text = person.name
                    }
                }
            }
        }
        val heading = document["h2"][0]!!
        println("Found heading.text: ${heading.text}")
        assertEquals("name1", heading.text)

        // now lets update the domain model and refresh the view
        person.name = "name2"
        bind.refresh()

        println("Found heading.text: ${heading.text}")
        assertEquals("name2", heading.text)
    }
}
