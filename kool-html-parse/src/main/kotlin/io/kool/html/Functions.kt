package io.kool.html

import java.io.*
import kotlin.dom.*
import org.apache.xerces.parsers.DOMParser
import org.apache.xml.serialize.HTMLSerializer
import org.apache.xml.serialize.OutputFormat
import org.cyberneko.html.HTMLConfiguration
import org.w3c.dom.*
import org.xml.sax.InputSource

/**
* Returns the element with the given id or throws an exception if it can't be found
*/
public fun Document.id(idValue: String): Element? {
    val element = getElementById(idValue)
    if (element != null) {
        return element
    } else {
        // OK lets try iterating through the elements instead
        for (e in elements) {
            val value = e.attribute("id")
            if (value == idValue) {
                return e
            }
        }
        return null
    }
}

/**
 * Creates a new HTML parser
 */
public fun createHtmlParser(): DOMParser {
    val config = HTMLConfiguration()
    config.setProperty("http://cyberneko.org/html/properties/names/elems", "lower")
    return DOMParser(config)
}

/**
 * Parses the given *inputStream* as a HTML document
 */
public fun parseHtml(inputStream: InputStream, parser: DOMParser = createHtmlParser()): Document {
    return parseHtml(InputSource(inputStream), parser)
}

/**
 * Parses the given *inputSource* as a HTML document
 */
public fun parseHtml(inputSource: InputSource, parser: DOMParser = createHtmlParser()): Document {
    parser.parse(inputSource)
    return parser.getDocument()!!
}

/**
 * Parses the given *uri* as a HTML document
 */
public fun parseHtml(uri: String, parser: DOMParser = createHtmlParser()): Document {
    parser.parse(uri)
    return parser.getDocument()!!
}

/**
 * Parses the given *file* as a HTML document
 */
public fun parseHtml(file: File, parser: DOMParser = createHtmlParser()): Document {
    return parseHtml(FileInputStream(file))
}


public fun defaultEncoding(): String = "UTF-8"

public fun defaultHtmlOutputFormat(): OutputFormat {
    return OutputFormat()
}


/**
 * Writes this document as HTML to the given *outputStream*
 */
public fun Document.writeHtml(outputStream: OutputStream, outputFormat: OutputFormat = OutputFormat(this)): Unit {
    val serializer = HTMLSerializer(outputStream, outputFormat)
    serializer.serialize(this)
}

/**
 * Writes this document as HTML to the given *writer*
 */
public fun Document.writeHtml(writer: Writer, outputFormat: OutputFormat = OutputFormat(this)): Unit {
    val serializer = HTMLSerializer(writer, outputFormat)
    serializer.serialize(this)
}

/**
 * Writes this document as HTML to the given *file*
 */
public fun Document.writeHtml(file: File, outputFormat: OutputFormat = OutputFormat(this)): Unit {
    val outputStream = FileOutputStream(file)
    outputStream.use {
        writeHtml(outputStream)
    }
}