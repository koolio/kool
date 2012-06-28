package test.kool.template.markdown

import junit.framework.TestCase
import io.kool.template.loadTextFilters
import kotlin.test.assertFalse

class MarkdownTest : TestCase() {
    fun testMarkdownTemplateLoads(): Unit {
        val list = loadTextFilters()
        assertFalse(list.isEmpty())
        for (f in list) {
            println("Found filter $f")
        }
    }
}