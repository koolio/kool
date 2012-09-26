package io.kool.template

import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStream
import java.io.Reader
import java.io.StringReader
import java.net.URL
import java.nio.charset.Charset

/**
* Represents an input
*/
abstract class Input() {

    /**
     * Returns the text of the input
     */
    abstract fun text(): String

    /**
     * Returns the [[Reader]] of the input
     */
    abstract fun reader(): Reader

    /**
     * Returns the [[InputStream]] of the intpu
     */
    abstract fun inputStream(): InputStream
}

/**
 * Abstract base class for implementation inheritence of [[TextInput]] implementations
 */
abstract class TextInputSupport: Input() {
    var charSet: Charset = defaultCharset

    override fun reader(): Reader = inputStream().reader(charSet)

    override fun text(): String = reader().readText()

}

class TextInput(val text: String): Input() {

    override fun inputStream(): InputStream {
        return ByteArrayInputStream(text.getBytes())
    }

    override fun reader(): Reader {
        return StringReader(text)
    }

    override fun text(): String {
        return text
    }
}

class ByteArrayInput(val bytes: ByteArray): TextInputSupport() {

    override fun inputStream(): InputStream {
        return ByteArrayInputStream(bytes)
    }

    override fun reader(): Reader {
        return inputStream().reader(charSet)
    }

    override fun text(): String {
        return String(bytes, charSet)
    }
}

class FileInput(val file: File): TextInputSupport() {

    override fun inputStream(): InputStream = FileInputStream(file)

    override fun reader(): Reader = FileReader(file)

    override fun text(): String = reader().readText()
}

class URLInput(val url: URL): TextInputSupport() {

    override fun inputStream(): InputStream = url.openStream()!!

    override fun reader(): Reader = inputStream().reader(charSet)

    override fun text(): String = url.readText(charSet)
}

class InputStreamInput(val inputStream: InputStream): TextInputSupport() {

    override fun inputStream(): InputStream = inputStream

}

