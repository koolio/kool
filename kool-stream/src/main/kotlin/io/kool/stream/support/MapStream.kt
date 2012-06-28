package io.kool.stream.support

import io.kool.stream.*
import java.io.Closeable
import java.util.concurrent.Executor
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ScheduledFuture


/**
 * Creates an [[Stream]] which transforms the handler using the given function
 */
class MapStream<T,R>(val delegate: Stream<T>, val fn: (Handler<R>) -> Handler<T>) : Stream<R>() {

    public override fun open(handler: Handler<R>): Cursor {
        val newHandler = (fn)(handler)
        val cursor = delegate.open(newHandler)
        newHandler.onOpen(cursor)
        return cursor
    }
}

/**
 * A [[Handler]] which filters elements in the stream
 */
class MapHandler<T, R>(delegate: Handler<R>, val transform: (T) -> R): DelegateHandler<T,R>(delegate) {

    public override fun onNext(next: T) {
        val result = (transform)(next)
        delegate.onNext(result)
    }
}