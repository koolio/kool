package io.kool.template

import java.io.*
import java.nio.charset.Charset

/**
* Represents an output which may be textual
*/
abstract class Output {
    /**
     * Returns the [[Writer]] of the Output
     */
    abstract fun writer(): Writer

    /**
     * Returns the [[OutputStream]] of the output
     */
    abstract fun outputStream(): OutputStream
}

abstract class TextOutputSupport : Output() {
    var charSet: Charset = defaultCharset

    override fun writer(): Writer = outputStream().writer(charSet)
}

class OutputStreamOutput(val out: OutputStream) : TextOutputSupport()  {
    override fun outputStream(): OutputStream = out
}

class FileOutput(val file: File) : TextOutputSupport() {

    override fun outputStream(): OutputStream = FileOutputStream(file)

    override fun writer(): Writer = FileWriter(file)
}

class ByteArrayOutput() : TextOutputSupport() {
    val buffer = ByteArrayOutputStream()

    override fun outputStream(): OutputStream = buffer

    fun toByteArray(): ByteArray = buffer.toByteArray()!!
}

