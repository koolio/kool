package test.kool.stream

import io.kool.stream.*

import org.junit.Test as test

class CollectionTest {

    test fun collectionAsStream() {
        val list = arrayList("foo", "bar")
        val stream = list.toStream()
        val closeable = stream.open{ println("String stream: $it") }
    }

    test fun collectionStreamFilterAndMap() {
        val list = arrayList("foo", "f", "bar")
        val stream = list.toStream().filter{ it.startsWith("f") }.map{ it.size }
        val closeable = stream.open{ println("Number stream: $it") }
    }


}