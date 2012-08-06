package io.kool.collection

import java.util.Collection
import java.io.Closeable
import io.kool.collection.support.FunctionCollectionEventListener

/**
 * Invokes the given block on every [[CollectionEvent<T>]]
 */
public inline fun <T> ObservableCollection<T>.onCollectionEvent(block: (CollectionEvent<T>) -> Unit): Closeable {
    return FunctionCollectionEventListener<T>(this, block)
}

/**
 * Returns an [[ObservableCollectionFacade<T>]] of this collection if it does not implement [[ObservableCollection<T>]]
 */
public inline fun <T> Collection<T>.observable(): ObservableCollection<T> {
    return if (this is ObservableCollection<*>) {
        this as (ObservableCollection<T>)
    } else {
        ObservableCollectionFacade<T>(this)
    }
}