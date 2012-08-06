package io.kool.collection

import java.util.Collection
import java.io.Closeable
import io.kool.collection.support.FunctionCollectionEventListener

/**
 * Represents an observable collection
 */
public trait ObservableCollection<T>: Collection<T> {

    public fun addCollectionEventListener(listener: CollectionEventListener<T>): Unit

    public fun removeCollectionEventListener(listener: CollectionEventListener<T>): Unit

}

