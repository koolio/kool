package test.kool.template.html.bind

import io.kool.template.html.*
import kotlin.dom.*
import kotlin.test.*
import org.junit.Test as test

class BindContentTest {
    test fun refreshBinding() {
        val person = Person("name1")

        val bind = Binder()

        val document = html {
            body {
                h2 {
                    bind {
                        text = "Orders for ${person.name}"
                    }
                }
                ul {
                    bind {
                        clear()
                        for (order in person.orders) {
                            if (order != null) {
                                li(order.productName)
                            }
                        }
                    }
                }
            }
        }
        val heading = document["h2"][0]!!
        println("Found heading.text: ${heading.text}")
        assertEquals("Orders for name1", heading.text)
        assertEquals(0, document["li"].size)

        // now lets update the domain model and refresh the view
        person.name = "name2"
        person.orders.add(Order(5, "beer"))
        bind.refresh()

        println("Found heading.text: ${heading.text}")
        assertEquals("Orders for name2", heading.text)
        assertEquals(1, document["li"].size)

        person.orders.add(Order(2, "wine"))
        bind.refresh()

        val list = document["li"]
        assertEquals(2, list.size)
        assertEquals("beer", list[0]?.text)
        assertEquals("wine", list[1]?.text)

        println(document.toXmlString())
    }
}
