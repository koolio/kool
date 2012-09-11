package test.kool.myapp

import io.kool.template.html.*
import java.util.Date
import kotlin.browser.*
import kotlin.dom.*
import org.w3c.dom.*

/**
* Entry point to my application which can be called
* from a JS browser when its ready, or from JavaFX
*/
fun myApp() {
    val app = MyApp()
    document["#view"][0]!!.appendChild(app.myTemplate())
}

class MyApp {
    val bind = Binder()
    var clickCount = 0

    fun myTemplate(): Node {
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
            button(id = "clickButton", text = "Click Me!") {
                onClick {
                    updateCounter()
                }
            }
        }
    }

    fun updateCounter() {
        ++clickCount
        bind.refresh()
    }
}