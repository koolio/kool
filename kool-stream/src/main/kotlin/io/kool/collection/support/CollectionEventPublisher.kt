package io.kool.collection.support

import io.kool.collection.ObservableCollection
import io.kool.collection.CollectionEventListener
import java.util.ArrayList
import io.kool.collection.CollectionEvent

/**
 * A default implementation of [[ObservableCollection<T>]] which manages listener registration
 * and provides helper methods for raising events
 */
public class CollectionEventPublisher<T>(val collection: ObservableCollection<T>): ObservableCollection<T> {
    private val listeners = ArrayList<CollectionEventListener<T>>()

    public override fun addCollectionEventListener(listener: CollectionEventListener<T>) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    public override fun removeCollectionEventListener(listener: CollectionEventListener<T>) {
        listeners.remove(listener)
    }

    /**
     * Fires the given event on all the current listeners
     */
    public fun fireEvent(event: CollectionEvent<T>) {
        for (listener in listeners) {
            listener.onCollectionEvent(event)
        }
    }

    public fun fireAddEvent(element: T) {
        fireEvent(CollectionEvent<T>(collection, element, CollectionEvent.Add))
    }

    public fun fireUpdateEvent(element: T) {
        fireEvent(CollectionEvent<T>(collection, element, CollectionEvent.Update))
    }

    public fun fireRemoveEvent(element: T) {
        fireEvent(CollectionEvent<T>(collection, element, CollectionEvent.Remove))
    }
}