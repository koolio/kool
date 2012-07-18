package io.kool.tools.htmlgen

import java.io.FileInputStream
import java.util.*
import kotlin.dom.*
import io.kool.html.*
import org.w3c.dom.*
import org.xml.sax.InputSource
import java.io.PrintWriter
import java.io.FileWriter
import java.io.File

fun main(args: Array<String>): Unit {
    val tool = GenerateHtmlModel()
    if (args.size > 0) {
        tool.htmlSpecUrl = args[0]
    }
    tool.run()
}

/**
 * Parses the HTML5 specification
 */
class GenerateHtmlModel: Runnable {

    public var specPrefix: String = "http://dev.w3.org/html5/spec/"
    public var htmlSpecUrl: String = specPrefix + "section-index.html"
    public var htmlGlobalAttributesUrl: String = specPrefix + "/global-attributes.html"

    public var outFileName : String = "../kool-template/src/main/kotlin/io/kool/template/html/GeneratedElements.kt"

    var identifierAliases = hashMap("type" to "typeName", "class" to "klass", "for" to "forInput", "object" to "objectElement", "var" to "varElement")

    var globalAttributes = ArrayList<String>()

    public override fun run() {
        println("Loading the HTML5 spec from $htmlSpecUrl")

        loadGlobalAttributes()

        val document = if (htmlSpecUrl.startsWith("file://")) {
            val file = htmlSpecUrl.substring("file://".length())
            val inputSource = InputSource(FileInputStream(file))
            parseHtml(inputSource)
        }
        else {
            parseHtml(htmlSpecUrl)
        }
        processDocument(document)
    }

    fun processDocument(document: Document): Unit {
        val title = document.id("elements-1")!!

        // lets find the next table element
        println("Found title ${title.text}")

        val table = title.nextElements().find{ it.getTagName() == "table" }
        println("Found table $table")
        if (table is Element) {
            val tbody = table["tbody"]
            if (tbody.notEmpty()) {
                useWriter { writer ->
                    val rows = tbody[0]["tr"]
                    for (row in rows) {
                        //println("Processing row $row")
                        val links = row["a"]
                        if (links.notEmpty()) {
                            val a = links.first!!
                            val name = a.text
                            var href = a.attribute("href")
                            if (href.notEmpty()) {
                                href = specPrefix + href
                            }
                            println("$name has href: $href")
                            val tds = row["td"]
                            var empty = false
                            if (tds.size() > 4) {
                                val description = tds[0]
                                val emptyTd = tds[3]
                                if (emptyTd.text == "empty") {
                                    empty = true
                                }
                                val attributesTd = tds[4]
                                val attributeNames = attributesTd["a"].drop(1).map<Element, String>{ it.text } + globalAttributes
                                println("Element $name empty $empty attributes $attributeNames")
                                val fnName = safeIdentifier(name)
                                val elementDescription = description.children().map{ it.toXmlString() }.makeString("")
                                val elementDescriptionText = description.children().map{ it.text }.makeString("")
                                val linkText = """<a href="$href" title="$elementDescriptionText">$name</a>"""
                                val safeIdentifiers = attributeNames.map { safeIdentifier(it) }

                                writer.println("/** Creates a new $linkText element: ${elementDescription} */")
                                writer.print("fun Node.$fnName(")
                                if (!empty) writer.print("text: String? = null, ")
                                for (id in safeIdentifiers) {
                                    writer.print("$id: String? = null, ")
                                }
                                writer.println("""init: Element.()-> Unit): Element {""")
                                writer.print("    val answer = ")
                                if (empty)
                                    writer.println("""element("$name", init)""")
                                else
                                    writer.println("""textElement("$name", text, init)""")

                                for (a in attributeNames) {
                                    val id = safeIdentifier(a)
                                    writer.println("""    if ($id != null) answer.setAttribute("$a", $id)""")
                                }
                                writer.println("    return answer")
                                writer.println("}")
                                writer.println("")

                                // lets avoid the use of the initialisation block
                                writer.println("/** Creates a new $linkText element: ${elementDescription} */")
                                writer.print("fun Node.$fnName(")
                                val args = ArrayList<String>()
                                var first = false
                                if (!empty) {
                                    args.add("text: String? = null")
                                }
                                for (id in safeIdentifiers) {
                                    args.add("$id: String? = null")
                                }
                                val arguments = safeIdentifiers.makeString(", ")
                                val textArgument = if (empty) "" else "text, "
                                writer.println("""${args.makeString(", ")}): Element = $fnName($textArgument$arguments) {}""")
                                writer.println("")
                            }
                        }
                    }
                }
            }
        } else {
            println("Could not find table!")
        }
        println("Done!")
    }

    protected fun useWriter(block: (PrintWriter) -> Any): Unit {
        println("Generating file $outFileName")
        val outFile = File(outFileName)
        outFile.getParentFile()!!.mkdirs()
        val writer = PrintWriter(FileWriter(outFile))
        writer.use {
            writer.println("""/*
 * NOTE - this file is autogenerated - do not edit!!!
 */

package io.kool.template.html

import org.w3c.dom.*

import kotlin.dom.*

""")

            block(writer)
        }
    }

    protected fun loadGlobalAttributes() {
        println("Parsing global attributes at $htmlGlobalAttributesUrl")
        val doc = parseHtml(htmlGlobalAttributesUrl)
        val header = doc.id("global-attributes")!!
        val ul = header.nextElements().find{ it.getTagName() == "ul" }!!
        println("Found ul $ul")
        val codes = ul["a"]
        for (code in codes) {
            val text = code.text
            println("Found global attribute $text")
            globalAttributes.add(text)
        }
    }

    protected fun safeIdentifier(name: String): String {
        return identifierAliases.getOrElse(name) {
            val paths = name.split("-")
            if (paths.size < 2) {
                name
            } else {
                // lets camelCase the string
                paths.fold(""){ a, b -> a + if (a.isEmpty()) b else b.capitalize() }
            }
        }
    }
}

