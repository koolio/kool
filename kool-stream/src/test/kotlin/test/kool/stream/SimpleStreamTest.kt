package test.kool.stream

import io.kool.stream.*
import kotlin.test.*
import org.junit.Test as test

class SimpleStreamTest {

    test fun subject() {
        var results = arrayList<String>()
        var results2 = arrayList<String>()

        val stream = SimpleStream<String>()
        val c1 = stream.open{ results += it }

        stream.onNext("foo")

        val c2 = stream.open{ results2 += it }

        stream.onNext("bar")
        c1.close()
        stream.onNext("another")
        c2.close()
        stream.onNext("foo")

        println("Stream generated: results1: $results and results2: $results2")
        assertEquals(arrayList("foo", "bar"), results)
        assertEquals(arrayList("bar", "another"), results2)
    }


}