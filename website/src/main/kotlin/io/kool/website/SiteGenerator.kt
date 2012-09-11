package io.kool.website

import io.kool.template.Template
import io.kool.template.renderTo
import java.io.File
import org.pegdown.Extensions
import org.pegdown.LinkRenderer
import org.pegdown.PegDownProcessor

class SiteGenerator(val sourceDir: File, val outputDir: File) : Runnable {
    public var markdownProcessor: PegDownProcessor = PegDownProcessor(Extensions.ALL)
    public var linkRendered: LinkRenderer = LinkRenderer()

    public override fun run() {
        println("Generating the site to $outputDir")

        sourceDir.recurse {
            if (it.isFile()) {
                var relativePath = sourceDir.relativePath(it)
                println("Processing ${relativePath}")
                var output: String? = null
                if (it.extension == "md") {
                    val text = processMacros(it, it.readText())
                    output = markdownProcessor.markdownToHtml(text, linkRendered) ?: ""
                    relativePath = relativePath.trimTrailing(it.extension) + "html"
                } else if (it.extension == "html") {
                    output = it.readText()
                }
                val outFile = File(outputDir, relativePath)
                outFile.directory.mkdirs()
                if (output != null) {
                    val template = layout(relativePath, it, output.sure())
                    template.renderTo(outFile)
                } else {
                    it.copyTo(outFile)
                }
            }
        }
    }

    /**
     * Applies a layout to the given file
     */
    fun layout(uri: String, file: File, text: String): Template {
        return DefaultLayoutTemplate(text)
    }

    fun error(message: String): Unit {
        println("ERROR: $message")
    }

    fun processMacros(owner: File, text: String): String {
        val start = "\${include(\""
        val end = "\")}"
        val buffer = StringBuilder()
        var idx = 0
        while (true) {
            val newIdx = text.indexOf(start, idx)
            val endIdx = if (newIdx >= 0) {
                text.indexOf(end, newIdx)
            } else -1
            if (newIdx < 0 || endIdx < 0) {
                buffer.append(text.substring(idx, text.length()))
                break
            } else {
                if (newIdx > idx) {
                    buffer.append(text.substring(idx, newIdx))
                }
                val fileName = text.substring(newIdx + start.size, endIdx)
                val file = File(owner.getParentFile(), fileName)
                try {
                    val fileText = file.readText()
                    buffer.append(fileText)
                } catch (e: Throwable) {
                    error("Failed to load file: ${file.getCanonicalPath()} due to $e")
                }
                idx = endIdx + end.size
            }
        }
        return buffer.toString()!!
    }
}