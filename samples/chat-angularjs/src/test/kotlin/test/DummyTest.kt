package test

import org.junit.Test as test
import io.kool.sample.chat.Products

class DummyTest {
    test fun annotationTest() {
        val klass = javaClass<Products>()
        val method = klass.getMethod("byId", javaClass<String>())!!
        println("Found method: $method")
        val array = method.getParameterAnnotations()
        for (paramAnns in array) {
            println("Parameter annotations ${paramAnns.toList()}")
        }
    }
}