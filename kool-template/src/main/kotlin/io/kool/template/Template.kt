package io.kool.template

/**
 * Represents a textual template
 */
import java.io.File
import java.io.FileWriter

trait Template {
    fun render(out: Appendable): Unit
}

/**
 * Renders the tiven template to a file
 */
fun Template.renderTo(file: File): Unit {
    val writer = FileWriter(file)
    writer.use {
        render(writer)
    }
}