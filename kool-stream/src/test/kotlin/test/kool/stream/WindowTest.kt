package test.kool.stream

import kotlin.test.*

import io.kool.stream.*

import org.junit.Test as test

import java.util.*

class WindowTest {

    test fun streamWithWindow() {
        var list = arrayList<Double>()
        for (i in 0..100) {
            list += i * 1.1
        }

        val window = list.toStream().window(4)
        window.take(9).open { println("Has window of $it") }
        Thread.sleep(2000)
    }
}