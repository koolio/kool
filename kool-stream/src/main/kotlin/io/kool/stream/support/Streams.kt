package io.kool.stream.support

import io.kool.stream.*
import java.io.Closeable
import java.util.concurrent.Executor
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ScheduledFuture


/**
 * A [[Stream]] which invokes the given function to open a stream and return a cursor
 */
class FunctionStream<T>(val fn: (Handler<T>) -> Cursor): Stream<T>() {
    fun toString() = "FunctionStream($fn)"

    public override fun open(handler: Handler<T>): Cursor {
        return (fn)(handler)
    }
}


/**
 * Converts a collection into an [[Stream]]
 */
class StreamCollection<T>(val coll: Iterable<T>, val executor: Executor) : Stream<T>() {
    public override fun open(handler: Handler<T>): Cursor {
        val subscription = IteratorTask(coll.iterator()!!, handler)
        executor.execute(subscription)
        return subscription
    }

}
