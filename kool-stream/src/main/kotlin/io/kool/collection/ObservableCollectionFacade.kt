package io.kool.collection

import java.util.Collection
import io.kool.collection.support.CollectionEventPublisher

/**
 * A facade which adds [[ObservableCollection<T>]] to a collection
 */
public class ObservableCollectionFacade<T>(protected val delegate: Collection<T>): Collection<T>, ObservableCollection<T> {
    val publisher = CollectionEventPublisher<T>(this)

    public override fun addCollectionEventListener(listener: CollectionEventListener<T>) {
        publisher.addCollectionEventListener(listener)
    }

    public override fun removeCollectionEventListener(listener: CollectionEventListener<T>) {
        publisher.removeCollectionEventListener(listener)
    }


    public override fun add(e: T): Boolean {
        return if (delegate.add(e)) {
            publisher.fireAddEvent(e)
            true
        } else false
    }

    public override fun addAll(c: Collection<out T>): Boolean {
        var answer = false
        for (e in c) {
            answer = answer || add(e)
        }
        return answer
    }

    public override fun clear() {
        val removals = delegate.toList()
        delegate.clear()
        for (e in removals) {
            publisher.fireRemoveEvent(e)
        }
    }

    public override fun contains(o: Any?): Boolean {
        return delegate.contains(o)
    }

    public override fun containsAll(c: Collection<out Any?>): Boolean {
        return delegate.containsAll(c)
    }

    public override fun equals(o: Any?): Boolean {
        return delegate.equals(o)
    }

    public override fun hashCode(): Int {
        return delegate.hashCode()
    }

    public override fun isEmpty(): Boolean {
        return delegate.isEmpty()
    }

    public override fun iterator(): java.util.Iterator<T> {
        return delegate.iterator()
    }
    public override fun remove(o: Any?): Boolean {
        return if (delegate.remove(o)) {
            publisher.fireRemoveEvent(o as T)
            true
        } else false
    }

    public override fun removeAll(c: Collection<out Any?>): Boolean {
        var answer = false
        for (e in c) {
            answer = answer || remove(e)
        }
        return answer
    }

    public override fun retainAll(c: Collection<out Any?>): Boolean {
        throw UnsupportedOperationException()
    }

    public override fun size(): Int {
        return delegate.size()
    }

    public override fun <R: Any?> toArray(a: Array<out R>): Array<R> {
        return delegate.toArray(a)
    }

    public override fun toArray(): Array<Any?> {
        return delegate.toArray()
    }
}