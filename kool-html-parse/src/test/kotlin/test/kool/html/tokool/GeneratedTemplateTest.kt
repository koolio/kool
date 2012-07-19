package test.kool.html.tokool

import dummyPackage.myTemplate
import kotlin.dom.*
import kotlin.test.*
import org.junit.Test as test

class GeneratedTemplateTest {
    test fun testTemplate() {
        val root = myTemplate()
        assertNotNull(root, "no template generated!")
        println("Generated ${root.toXmlString()}")
    }
}