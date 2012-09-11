package test.kool.html.tokool

import org.junit.Test as test
import io.kool.angular.generate.main

class ModelGeneratorTest {
    test fun generator() {
        main(array("src/test/resources/templates"))
    }
}