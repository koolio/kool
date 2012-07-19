package test.kool.html.tokool

import io.kool.html.tokool.main
import org.junit.Test as test

class HtmlToKoolTest {
    test fun testHtmlToKool() {
        main(array("src/test/resources/sample.html", "dummyPackage", "myTemplate", "src/test/kotlin/generated/GeneratedTemplate.kt"))
    }
}