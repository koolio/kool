package test.kool.stream

import kotlin.test.*

import io.kool.stream.*

import org.junit.Test as test

class DistinctTest {

    test fun subject() {
        var results = arrayList<String>()

        val stream = SimpleStream<String>()
        val c1 = stream.distinct().open{ results += it }

        stream.onNext("foo")
        stream.onNext("foo")
        stream.onNext("bar")
        stream.onNext("bar")
        stream.onNext("foo")
        c1.close()

        println("Stream generated: results1: $results")
        assertEquals(arrayList("foo", "bar", "foo"), results)
    }


}