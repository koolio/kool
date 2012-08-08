package io.kool.collection

import java.util.Collection
import java.io.Closeable
import io.kool.collection.support.FunctionCollectionEventListener
import org.w3c.dom.Element

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


/**
 * Allow observable collections to be bound to a DOM element
 */
fun <T> Element.repeat(collection: ObservableCollection<T>, block: (T)-> Unit): Unit {
    // lets render the current collection
    for (e in collection) {
        // TODO we need to detect if a new element is added so that we can zap it based on the removal events
        (block)(e)
    }
    collection.onCollectionEvent {
        println("===== got collection change event ${it.kindText} on ${it.element}")
        if (it.isRemove()) {
            // lets remove all the child elements with the same id
        } else {
            // lets add/update the child elements with the element
            (block)(it.element)
        }
    }
}