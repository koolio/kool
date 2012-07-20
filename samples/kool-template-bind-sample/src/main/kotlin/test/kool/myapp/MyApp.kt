package test.kool.myapp

import io.kool.template.html.*
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.events.*
import kotlin.browser.*
import kotlin.dom.*
import java.util.Date

fun <T: Event> eventHandler(eventType: Class<T>, handler: (T) -> Unit): EventListener {
    return object : EventListener {
        public override fun handleEvent(e: Event?) {
            if (e != null && eventType.isInstance(e)) {
                handler(e as T)
            }
        }
    }
}

fun Node?.onClick(capture: Boolean = false, handler: (MouseEvent) -> Unit): Unit {
    if (this is EventTarget) {
        addEventListener("click", eventHandler(javaClass<MouseEvent>(), handler), capture)
    }
}

/**
 * Entry point to my application which can be called
 * from a JS browser when its ready, or from JavaFX
 */
fun myApp() {
    val app = MyApp()
    document["#view"][0]!!.appendChild(app.myTemplate())

    document["#clickButton"][0].onClick {
        app.onButtonClick()
    }
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
            button(id = "clickButton", text = "Click Me!")
        }
    }

    fun onButtonClick() {
        ++clickCount
        bind.refresh()
    }
}