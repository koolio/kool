package test.kool.template.languageinjection

import org.junit.Test as test
import org.intellij.lang.annotations.Language

class DummyTest {
    test fun useFunction() {
        val html = makeHtml()
    }

    test fun useExpression() {
        // TODO runtime error: java.lang.InstantiationError: org.intellij.lang.annotations.Language
        // Language("HTML")
        val html = "<html></html>"
    }

    Language("HTML") fun makeHtml(): String = "<html></html>"
}