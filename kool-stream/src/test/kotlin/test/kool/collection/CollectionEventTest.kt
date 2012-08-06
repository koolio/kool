package test.kool.collection

import io.kool.collection.ObservableCollectionFacade
import io.kool.collection.onCollectionEvent
import java.util.ArrayList
import kotlin.test.*
import org.junit.Test as test

class CollectionEventTest {
    test fun eventsFiredCorrectly() {
        val results = ArrayList<String>()

        val collection = ObservableCollectionFacade<String>(ArrayList<String>())

        collection.onCollectionEvent {
            results.add("${it.kindText}:${it.element}")
        }

        collection.add("a")
        collection.add("b")

        assertEquals(arrayList("Add:a", "Add:b"), results)

        collection.clear();
        collection.add("c")
        collection.add("d")
        collection.remove("c")
        collection.remove("doesNotExist")

        assertEquals(arrayList("Add:a", "Add:b", "Remove:a", "Remove:b", "Add:c", "Add:d", "Remove:c"), results)
    }
}