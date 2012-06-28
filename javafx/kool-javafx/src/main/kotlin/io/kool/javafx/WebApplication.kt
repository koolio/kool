package io.kool.javafx

import java.io.File
import javafx.application.Application
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker.State
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import javafx.stage.Stage
import kotlin.browser.*
import kotlin.dom.*
import org.w3c.dom.Document

/**
* An [[Application]] which uses a [[WebView]] and [[WebEngine]]
*/
public open class WebApplication(): Application() {
    private var _engine: WebEngine? = null

    var engine: WebEngine
        get() = _engine!!
        set(value) {
            _engine = value
        }

    var prefWidth = 800.0
    var prefHeight = 600.0

    /**
     * Registers a function block to be invoked when the browser is ready and the page has been loaded
     */
    public fun ready(fireOnce: Boolean = false, block: (Document) -> Any?): Unit {
        val changeListener = object : ChangeListener<State?> {
            public override fun changed(observable: ObservableValue<out State?>?, oldValue: State?, newValue: State?) {
                if (newValue != null && newValue == State.SUCCEEDED) {
                    val doc = engine.getDocument()
                    if (doc != null) {
                        block(doc)
                    }
                    if (fireOnce) {
                        engine.getLoadWorker()?.stateProperty()?.removeListener(this)
                    }
                }
            }
        }
        engine.getLoadWorker()?.stateProperty()?.addListener(changeListener)
    }

    override public fun start(primaryStage: Stage?) {
        val view = WebView()
        engine = view.getEngine()!!

        view.setMinSize(100.0, 100.0)
        view.setPrefSize(prefWidth, prefHeight)
        view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        ready {
            // lets register the global DOM instance
            document = it
            loadKotlinScripts(it)
        }
        val initialLocation = loadInitial()

        val locationField = TextField(initialLocation)
        locationField.setMaxHeight(Double.MAX_VALUE)
        val goButton = Button("Go")
        goButton.setDefaultButton(true)
        val goAction = object : EventHandler<ActionEvent?> {
            public override fun handle(event: ActionEvent?) {
                val location = locationField.getText() ?: ""
                if (location.notEmpty()) {
                    val fullUrl = if (location.startsWith("http://") || location.contains("://")) location else
                        "http://" + location
                    load(fullUrl)
                }
            }
        }
        goButton.setOnAction(goAction)
        locationField.setOnAction(goAction)
        val changeListener = object : ChangeListener<String?> {
            public override fun changed(observable: ObservableValue<out String?>?, oldValue: String?, newValue: String?) {
                locationField.setText(newValue)
            }
        }
        engine.locationProperty()?.addListener(changeListener)
        val grid = GridPane()
        grid.setVgap(5.0)
        grid.setHgap(5.0)
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        //grid.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        GridPane.setConstraints(locationField, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.SOMETIMES)
        GridPane.setConstraints(goButton, 1, 0)
        GridPane.setConstraints(view, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS)

        val buttonWidth = 40.0
        var gridConstraints = grid.getColumnConstraints()!!
        gridConstraints.add(ColumnConstraints(100.0, prefWidth - (buttonWidth + (2 * grid.getHgap())), Double.MAX_VALUE, Priority.ALWAYS, HPos.CENTER, true))
        gridConstraints.add(ColumnConstraints(buttonWidth, buttonWidth, Double.MAX_VALUE, Priority.SOMETIMES, HPos.CENTER, true))

        grid.getChildren()?.addAll(locationField, goButton, view)


        val scene = Scene(grid)
        val stage = primaryStage!!
        stage.setMaxHeight(Double.MAX_VALUE)
        stage.setMaxWidth(Double.MAX_VALUE)
        stage.setHeight(prefHeight + buttonWidth + (2 * grid.getVgap()))
        stage.setWidth(prefWidth)
        stage.setScene(scene)
        stage.show()
    }

    /**
     * Strategy method to load the initial URL and returning the URL
     * so it can be populated into the address bar
     */
    protected open fun loadInitial(): String {
        val parameters = getParameters()!!
        val unnamed = parameters.getUnnamed()!!
        val raw = parameters.getRaw()!!
        val url = if (unnamed.notEmpty()) {
            unnamed.first()!!
        } else if (raw.notEmpty()) {
            raw.first()!!
        } else {
            "http://kool.io/"
        }
        return load(url)
    }

    /**
     * Loads the given URL
     */
    fun load(val url: String): String {
        val filePrefix = "file://"
        if (url.startsWith(filePrefix)) {
            val fileName = url.trimLeading(filePrefix)
            println("Loading file: " + fileName)
            val content = File(fileName).readText()
            engine.loadContent(content)
        } else {
            println("Loading URL: " + url)
            engine.load(url)
        }
        return url
    }

    /**
     * Attempts to find any &lt;script&gt; tags for the type *text/kotlin* and invokes the methods
     */
    protected fun loadKotlinScripts(document: Document): Unit {
        val scriptTags = document["script"]
        for (scriptTag in scriptTags) {
            val t = scriptTag.attribute("type")
            if (t == "text/kotlin") {
                val text = scriptTag.text.trim()
                val lines = text.split("\n")
                for (line in lines) {
                    val initCall = line.trim().trimTrailing("()")
                    if (initCall.notEmpty()) {
                        val idx = initCall.lastIndexOf('.')
                        if (idx <= 0) {
                            println("Warning cannot invoke initCall which is not on a class $initCall")
                        } else {
                            try {
                                val className = initCall.substring(0, idx)
                                val methodName = initCall.substring(idx + 1)
                                val klass = loadClass<Any>(className)
                                val method = klass.getMethod(methodName)!!
                                method.invoke(null)
                            } catch (e: Exception) {
                                println("Failed to invoke startup method: " + initCall + ". Reason: " + e)
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    protected fun <T> loadClass(className: String): Class<T> {
        return Class.forName(className) as Class<T>
    }
}

