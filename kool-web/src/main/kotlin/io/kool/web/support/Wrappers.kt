package io.kool.web.support

import java.io.*
import javax.servlet.*
import javax.servlet.http.*

import io.kool.template.ByteArrayInput
import io.kool.template.FilterContext
import io.kool.template.Output
import io.kool.template.Template
import io.kool.web.filterContext



class ResponseOutput(val response: ServletResponse): Output() {

    public override fun outputStream(): OutputStream {
        return response.getOutputStream().sure()
    }

    public override fun writer(): Writer {
        return response.getWriter().sure()
    }
}

class BufferedResponseWrapper(response: HttpServletResponse): HttpServletResponseWrapper(response) {
    val buffer = ByteArrayOutputStream();
    private val writer = PrintWriter(OutputStreamWriter(buffer))

    fun toBytes(): ByteArray {
        writer.flush()
        return buffer.toByteArray().sure()
    }

    public override fun getWriter(): PrintWriter {
        return writer
    }

    public override fun getOutputStream(): ServletOutputStream? {
        return BufferedServletOutputStream(buffer)
    }

    public override fun setContentLength(len: Int) {
        // avoid as we typically may write something else
    }
}

class BufferedServletOutputStream(val buffer: ByteArrayOutputStream) : ServletOutputStream() {

    public override fun write(b: Int) {
        buffer.write(b)
    }
}

class TextBufferResponseWrapper(response: HttpServletResponse): HttpServletResponseWrapper(response) {
    val output = StringWriter();

    fun getOutputText(): String {
        return output.toString().sure()
    }

    public override fun getWriter(): PrintWriter {
        return PrintWriter(output);
    }

    public override fun setContentLength(len: Int) {
        // avoid as we typically may write something else
    }
}
