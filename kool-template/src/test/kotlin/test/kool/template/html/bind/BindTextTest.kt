package test.kool.template.html.bind

import io.kool.template.html.*
import kotlin.dom.*
import kotlin.test.*
import org.junit.Test as test

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
