package test.kool.stream

import io.kool.stream.*
import java.util.Timer
import org.junit.Test as test

class TimerTest {

    test fun subject() {
        var results = arrayList<Long>()

        val timer = Timer()
        val stream = timer.fixedDelayStream(1000).take(3).forEach {
            println("Timer fired at $it")
            results += it
        }
        val mock = MockHandler<Long>()
        mock.expectReceive(3)
        stream.open(mock)
        mock.assertExpectations()
        mock.assertWaitForClose()

        println("Interval Stream generated: results: $results")
        println("Mock events: ${mock.events}")
    }


}