package test.kool.stream

import io.kool.stream.*
import kotlin.test.*
import org.junit.Test as test

class TakeTest {

    test fun takeFromStream() {
        var results = arrayList<String>()
        val stream = SimpleStream<String>()

        val c1 = stream.take(1).open{ results += it }

        stream.onNext("foo")

        c1.assertClosed()

        println("Stream generated: results1: $results")
        assertEquals(arrayList("foo"), results)
    }


    test fun takeWhileFromStream() {
        var results = arrayList<String>()
        val stream = SimpleStream<String>()

        val c1 = stream.takeWhile{ it.startsWith("f") }.open{ results += it }

        stream.onNext("foo")
        stream.onNext("foo2")
        stream.onNext("bar")

        c1.assertClosed()

        println("Stream generated: results1: $results")
        assertEquals(arrayList("foo", "foo2"), results)

    }

}