package test.kool.myapp

import io.kool.template.html.*
import org.w3c.dom.*
import kotlin.browser.*
import kotlin.dom.*
import java.util.Date
import com.mongodb.Mongo
import io.kool.mongodb.*
import io.kool.collection.*

/**
 * Entry point to my application which can be called
 * from a JS browser when its ready, or from JavaFX
 */
fun myApp() {
    val app = MyApp()
    document["#view"][0]!!.appendChild(app.myTemplate())
}

class MyApp {
    val mongo = Mongo("127.0.0.1")!!
    val db = mongo.getDB("koolioSample")!!
    val people = db.observableCollection("people")

    val bind = Binder()
    var clickCount = 0

    fun myTemplate(): Node {
        if (people.isEmpty()) {
            // lets add a few people
            people.add(dbObject("name" to "James", "city" to "Mells"))
            people.add(dbObject("name" to "Hiram", "city" to "Tampa"))
        }

        return document.div(id = "newDivId") {
            h1("Kool Template Binding Example")
            p {
                text("the button has been clicked ")
                b {
                    bind { text = "$clickCount" }
                }
                text(" times at ")
                b {
                    bind { text = "" + Date() }
                }
            }

            ul {
                repeat(people) {
                    li("${it["name"]} lives at ${it["city"]}")
                }
            }

            h3("Markup")
            table {
                tr {
                    td {
                        button(id = "clickButton", text = "Show HTML") {
                            onClick {
                                updateCounter()
                            }
                        }
                    }
                }
                tr {
                    td {
                        textarea(rows = "30", cols = "80") {
                            bind { text = document.getElementById("newDivId")?.toXmlString() ?: "" }
                        }
                    }
                }
            }
        }
    }

    fun updateCounter() {
        ++clickCount
        bind.refresh()
    }
}