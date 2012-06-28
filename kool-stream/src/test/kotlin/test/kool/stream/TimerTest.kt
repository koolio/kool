package test.kool.stream

import io.kool.stream.*
import java.util.Timer

import kotlin.test.*
import org.junit.Test as test

class TimerTest {

    test fun subject() {
        var results = arrayList<Long>()

        val timer = Timer()
        val c1 = timer.fixedDelayStream(1000).take(3).open {
            println("Timer fired at $it")
            results += it
        }

        Thread.sleep(5000)
        println("Interval Stream generated: results: $results")
    }


}