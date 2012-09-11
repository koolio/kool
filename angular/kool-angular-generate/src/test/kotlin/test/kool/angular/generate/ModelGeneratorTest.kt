package test.kool.html.tokool

import io.kool.angular.generate.main
import org.junit.Test as test

class ModelGeneratorTest {
    test fun generator() {
        main(array("src/test/resources/templates"))
    }
}