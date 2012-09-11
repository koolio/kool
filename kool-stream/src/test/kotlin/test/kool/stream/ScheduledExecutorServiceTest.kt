package test.kool.stream

import io.kool.stream.*
import java.util.concurrent.Executors
import org.junit.Test as test

class ScheduledExecutorServiceTest {

    test fun subject() {
        var results = arrayList<Long>()

        val executor = Executors.newSingleThreadScheduledExecutor()!!

        val c1 = executor.scheduleAtFixedRateStream(1000).take(4).open {
            println("Timer fired at $it")
            results += it
        }

        Thread.sleep(5000)
        println("Interval Stream generated: results: $results")
    }


}