package io.kool.collection

/**
 * Represents a listener of collection events
 */
public trait CollectionEventListener<T> {
    fun onCollectionEvent(event: CollectionEvent<T>): Unit
}


