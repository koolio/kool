package io.kool.stream

import io.kool.stream.support.*

import java.io.Closeable
import java.util.ArrayList

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * A simple [[Stream]] that can have elements injected into its active [[Handler]] instances
 */
public open class SimpleStream<in T>(val handlers: ConcurrentContainer<Handler<T>> = DefaultConcurrentContainer<Handler<T>>()): Stream<T>() {

    override fun open(handler: Handler<T>): Cursor {
        handlers.add(handler)
        val cursor = object: AbstractCursor() {
            protected override fun doClose() {
                handlers.remove(handler)
            }
        }
        handler.onOpen(cursor)
        return cursor
    }

    public open fun onComplete(): Unit {
        handlers.forEach{ it.onComplete() }
    }

    public open fun onError(e: Throwable): Unit {
        handlers.forEach{ it.onError(e) }
    }

    public open fun onNext(next: T): Unit {
        handlers.forEach{ it.onNext(next) }
    }
}