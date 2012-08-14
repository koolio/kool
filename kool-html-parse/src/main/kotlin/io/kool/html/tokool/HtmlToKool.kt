package io.kool.html.tokool

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.LinkedHashMap

import kotlin.dom.*
import org.w3c.dom.*
import io.kool.html.parseHtml

/**
 * A main which converts a file/uri into a [Kool Template](http://kool.io/templates.html)
 */
fun main(args: Array<String>) {
    if (args.size < 3) {
        println("Usage: htmlFileOrUri packageName functionName [outputFile]")
    } else {
        val fileOrUri = args[0]
        val packageName = args[1]
        val functionName = args[2]
        val outputFile = if (args.size > 3) {
            args[3]
        } else {
            val packagePath = packageName.replace('.', '/')
            val dir = if (packagePath.size > 0) packagePath + "/" else ""
            "$dir$functionName.kt"
        }
        writeHtmlAsKoolTemplate(fileOrUri, packageName, functionName, outputFile)
        println("Generated file $outputFile")
    }
}

/**
 * Converts a file/uri into a [Kool Template](http://kool.io/templates.html)
 */
public fun writeHtmlAsKoolTemplate(fileOrUri: String, packageName: String, functionName: String, outputFile: String) {
    val file = File(fileOrUri)
    val doc = if (file.exists() && file.isFile()) {
        parseHtml(file)
    } else parseHtml(fileOrUri)

    val outFile = File(outputFile)
    outFile.getParentFile()?.mkdirs()
    val writer = PrintWriter(FileWriter(outFile))
    writer.use {
        writeHtmlAsKoolTemplate(doc, writer, packageName, functionName)
    }
}

/**
 * Converts the given HTML document into a [Kool Template](http://kool.io/templates.html)
 */

public fun writeHtmlAsKoolTemplate(doc: Document, writer: PrintWriter, packageName: String, functionName: String) {
    writeHtmlAsKoolTemplate(doc, writer, """package $packageName

import kotlin.dom.*
import io.kool.template.html.*
import org.w3c.dom.*

public fun $functionName(doc: Document = createDocument()): Element = doc.""")
}

public fun writeHtmlAsKoolTemplate(doc: Document, writer: PrintWriter, header: String) {
    writer.print(header)
    val element = doc.getDocumentElement()
    if (element != null) {
        writeHtmlElementAsKoolTemplate(element, writer, 0)
    }
}

public fun writeHtmlElementAsKoolTemplate(element: Element, writer: PrintWriter, indent: Int) {
    indent(writer, indent)
    writer.print("${element.localName}")
    val attributeMap = LinkedHashMap<String, String>()

    // lets filter out pure whitespace text nodes
    val children = element.children().filter<Node> { if (it is Text) !it.text.trim().isEmpty() else true }
    // lets treat single children text nodes as a parameter
    if (children.size == 1) {
        val firstChild = children[0]
        if (firstChild is Text) {
            attributeMap["text"] = firstChild.text
            children.clear()
        }
    }
    val attributes = element.attributes
    if (attributes != null) {
        val size = attributes.length
        for (i in 0.rangeTo(size)) {
            val attr = attributes.item(i)
            if (attr != null) {
                attributeMap[attr.localName] = attr.text
            }
        }
    }

    if (attributeMap.size > 0) {
        writer.print("(")
        val text = attributeMap["text"]
        if (attributeMap.size == 1 && text != null) {
            // lets not bother naming this parameter
            writer.print("\"")
            writer.print(text)
            writer.print("\"")
        } else {
            var first = true
            for (e in attributeMap) {
                if (e != null) {
                    val name = e.key
                    val value = e.value
                    if (value.size > 0) {
                        if (first)
                            first = false
                        else
                            writer.print(", ")
                        writer.print(name)
                        writer.print(" = \"")
                        writer.print(value)
                        writer.print("\"")
                    }
                }
            }
        }
        writer.print(")")
    }
    if (children.isEmpty()) {
        writer.println()
    } else {
        writer.println(" {")
        val childIndent = indent + 1
        for (child in children) {
            if (child is Element) {
                writeHtmlElementAsKoolTemplate(child, writer, childIndent)
            } else if (child is Text) {
                indent(writer, indent)
                writer.println("""text("${child.text}")""")
            }
        }
        indent(writer, indent)
        writer.println("}")
    }
}

fun indent(writer: PrintWriter, indent: Int) {
    for (i in 1.rangeTo(indent)) {
        writer.print("    ")
    }
}
