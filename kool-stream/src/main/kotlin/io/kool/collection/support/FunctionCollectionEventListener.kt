package io.kool.collection.support

import io.kool.collection.CollectionEvent
import io.kool.collection.CollectionEventListener
import io.kool.collection.ObservableCollection
import java.io.Closeable

/**
 * Allows a [[CollectionEventListener<T>]] to be implemented using a function block which can be closed to remove the listener
 */
public class FunctionCollectionEventListener<T>(val collection: ObservableCollection<T>,
                                                val block: (CollectionEvent<T>) -> Unit): CollectionEventListener<T>, Closeable {

    {
        collection.addCollectionEventListener(this)
    }

    public override fun toString(): String = "FunctionCollectionEventListener($collection, $block)"

    override fun onCollectionEvent(event: CollectionEvent<T>) {
        (block)(event)
    }

    public override fun close() {
        collection.removeCollectionEventListener(this)
    }
}