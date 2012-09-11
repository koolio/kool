package test.kool.template.markdown

import io.kool.template.loadTextFilters
import junit.framework.TestCase
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