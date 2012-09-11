package io.kool.angular.generate

import kotlin.dom.*
import kotlin.io.*
import io.kool.html.*
import java.io.File
import org.w3c.dom.Element
import io.kool.angular.generate.ClassDefinition
import io.kool.angular.generate.TypeDefinition
import org.w3c.dom.DOMLocator

/**
 * Generates a typesafe Kotlin model for AngularJS templates
 * to ease developement of Kotlin based controllers for AngularJS
 */
class ModelGenerator(val args: Array<String>) {
    val model = ClassDefinition("model")
    var _controller: ClassDefinition? = null
    protected var source: Element? = null
    protected var sourceFile: File? = null

    var fileFilter: (File) -> Boolean = { (f: File) ->
        f.getName()?.endsWith(".html") ?: false
    }

    fun run(): Unit {
        if (args.isEmpty()) {
            println("Require a directory to scan")
            return
        }
        val dir = args[0]
        scanDirectory(File(dir))
    }

    fun scanDirectory(dir: File): Unit {
        val files = dir.listFiles()
        for (file in files) {
            if (file != null) {
                if (file.isDirectory()) {
                    scanDirectory(file)
                } else if (fileFilter(file)) {
                    processFile(file)
                }
            }
        }

        println("Model: $model")
        //dumpClasses(model)
    }

    protected fun dumpClasses(c: ClassDefinition, ident: String = "") {
        println("${ident}class $c.name {")
        val cident = ident + "  "
        for (e in c.members) {
            println("${cident}e.key = $e.value")
        }
        println("${ident}}")
    }

    protected fun processFile(file: File) {
        val doc = parseHtml(file)
        println("Found doc $doc for file ${file.getPath()}")
        val elem = doc.documentElement
        if (elem != null) {
            processElement(file, elem)
        }
    }

    protected fun processElement(file: File, element: Element) {
        fun processChildren() {
            val text = element.childrenText
            if (text != null) {
                //println("element $element has text $text")
                var pivot = 0
                while (true) {
                    val idx = text.indexOf("{{", pivot)
                    if (idx < 0) break
                    val idx2 = text.indexOf("}}", idx + 2)
                    if (idx2 < 0) break
                    val expr = text.substring(idx + 2, idx2)
                    //println("Found expression: {{$expr}}" )
                    addModel(file, null, expr)
                    pivot = idx2 + 2
                }
                for (e in element.childElements()) {
                    processElement(file, e)
                }
            }
        }
        sourceFile = file
        source = element

        val c = element.ngAttribute("ng-controller")
        if (c != null && c.notEmpty()) {
            _controller = model.classDefinition(c)
            println("Found controller $_controller")
            processChildren()
            println("Cleared controller on ${element}")
            _controller = null
        } else {
            val m = element.ngAttribute("ng-model")
            if (m != null && m.notEmpty()) {
                addModel(file, element, m)
            }
            processChildren()
        }
    }

    val controller: ClassDefinition
    get() {
        require(_controller != null) {
            val s = source
            val msg = if (s is DOMLocator)
                "${s.getLineNumber()}:${s.getColumnNumber()}"
            else "${s?.toXmlString()}"
            "No controller defined in this scope ${sourceFile}@${msg}"
        }
        return _controller!!
    }

    protected fun addModel(file: File, element: Element?, expression: String) {
        fun createPrimitiveType(name: String): TypeDefinition {
            //println("Creating primitive type for name $name from expression: $expression")
            return if (element != null && element.hasAttribute("smart-float")) {
                FloatDefinition
            } else {
                StringDefinition
            }
        }

        val paths = expression.split("\\.")
        if (paths != null && paths.size > 1) {
            var cd = controller
            var idx = 0
            for (path in paths) {
                if (++idx < paths.size) {
                    cd = cd.classDefinition(path)
                } else {
                    cd.members.getOrPut(path) { createPrimitiveType(path) }
                }
            }
        } else {
            controller.members.getOrPut(expression) { createPrimitiveType(expression) }
        }
    }



}