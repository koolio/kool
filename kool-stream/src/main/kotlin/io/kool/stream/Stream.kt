package io.kool.stream

import java.io.Closeable
import io.kool.stream.support.*
import java.util.Queue
import java.util.List
import java.util.Map

/**
 * Represents an asynchronous stream of events which can be composed and processed asynchronously.
 *
 * You can think of a *Stream* as being like a push based collection where rather than pulling values out,
 * values are pushed into a [[handler]] instead so processing of collections can be done completely asynchronously.
 */
public abstract class Stream<out T> {

    /**
     * Opens the stream for processing with the given handler
     */
    abstract fun open(handler: Handler<T>): Cursor

    /**
     * Opens the stream of events using the given function block to process each event
     * until the stream completes or fails
     */
    public open fun open(nextBlock: (T) -> Unit): Cursor {
        return open(FunctionHandler(nextBlock))
    }

    /**
     * Opens the stream of events using the given function block to process each event
     * returning *false* if the next event cannot be processed yet to allow flow control to
     * kick in
     */
    public open fun openNonBlockingCursor(nextBlock: (T) -> Boolean): Cursor {
        return open(FunctionNonBlockingHandler(nextBlock))
    }

    /**
     * Opens the stream of events using a [[NonBlockingHandler]] so that flow control
     * can be used to suspend the stream if the handler cannot consume an offered next event.
     *
     * [[Stream]] implementation classes which can implement flow control should override this
     * function to provide support for the [[NonBlockingCursorCursor]]
     */
    public open fun open(suspendableHandler: NonBlockingHandler<T>): NonBlockingCursor {
        val handler = NonBlockingHandlerAdapter(suspendableHandler)
        val cursor = open(handler)
        return cursor.toNonBlockingCursorCursor()
    }

    /**
     * Returns a new [[Stream]] which filters out elements
     * in the stream to those which match the given predicate
     */
    fun filter(predicate: (T) -> Boolean): Stream<T>  {
        return DelegateStream(this) {
            FilterHandler(it, predicate)
        }
    }

    /**
     * Returns a [[Stream]] which consumes events from *this* stream and *that* stream
     * and then raises events of type [[#(A,B)]] when there is an event on *stream1* followed by an event on *stream2*
     *
     * We filter out consecutive events on *this* stream or events on *that* stream before there is an event on *this*.
     */
    fun <R> followedBy(stream: Stream<R>): Stream<#(T,R)> {
        return FollowedByStream(this, stream)
    }

    /**
     * Returns a new [[Stream]] which transforms the elements
     * in the stream using the given function
     */
    fun <R> map(transform: (T) -> R): Stream<R> {
        return MapStream<T,R>(this) {
            MapHandler<T,R>(it, transform)
        }
    }

    /**
     * Returns a [[Stream]] which combines events from *this* stream and *that* stream
     * and then raises events of type [[#(T,R)]] when there is an event on either *stream1* or on *stream2*
     * such that there is always a value of each event.
     *
     * If there are multiple events on *this* stream then the previous event on *that* stream will be included
     */
    fun <R> and(stream: Stream<R>): Stream<#(T,R)> {
        return AndStream(this, stream)
    }

    /**
     * Returns a [[Stream]] which merges events from *this* stream and *that* stream
     * and then raises events of type [[#(T?,R?)]] when there is an event on either *stream1* or on *stream2*
     *
     * When there is an event on *this* stream then the event on *that* will be null and vice versa
     */
    fun <R> merge(stream: Stream<R>): Stream<#(T?,R?)> {
        return MergeStream(this, stream)
    }

    /**
     * Returns a [[Stream]] which filters out duplicates
     */
    fun distinct(): Stream<T> {
        var previous: T? = null
        return filter  {
            val changed = previous == null || previous != it
            previous = it
            changed
        }
    }

    /**
     * Returns a [[Stream]] which takes the given amount of items from
     * the stream then closes it
     */
    fun take(n: Int): Stream<T> {
        var counter = n
        return TakeWhileStream(this, true){ --counter > 0 }
    }

    /**
     * Returns a [[Stream]] which takes the events from this stream until
     * the given predicate returns false and the underlying stream is then closed
     */
    fun takeWhile(predicate: (T) -> Boolean): Stream<T> {
        return TakeWhileStream(this, false, predicate)
    }

    /**
     * Returns a [[Stream]] which consumes events and puts them into a moving time window
     * of a given number of *millis* which then fires the window [[List]] of elements into the handler on each event
     */
    fun timeWindow(millis: Long): Stream<List<T>> {
        return TimeWindowStream(this, millis)
    }

    /**
     * Returns a [[Stream]] which consumes events and puts them into a moving window
     * of a fixed *size* which then fires the window [[List]] of elements into the handler on each event
     */
    fun window(size: Int): Stream<List<T>> {
        return WindowStream(this, size)
    }

    /**
     * Helper method to open a delegate stream
     */
    protected fun openDelegate(delegate: Stream<T>, handler: Handler<T>): Cursor {
        val cursor = delegate.open(handler)
        handler.onOpen(cursor)
        return cursor
    }

    class object {

        /**
         * Create an empty [[Stream]]
         */
        fun <T> empty(): Stream<T> = FunctionStream {
            it.onComplete()
            DefaultCursor()
        }

        /**
         * Create an [[Stream]] of a single value
         */
        fun <T> single(next: T): Stream<T> = FunctionStream {
            it.onNext(next)
            it.onComplete()
            DefaultCursor()
        }

        /**
         * Create an [[Stream]] which always raises an error
         */
        fun <T> error(exception: Throwable): Stream<T> = FunctionStream {
            it.onError(exception)
            DefaultCursor()
        }

    }
}

/**
 * Transforms the Stream<Iterable<T>> by grouping the contents of each stream element into a [[Map<K, List<T>>]]
 * using the given *toKey* function to calculate the key to use in the map for each value
 */
public fun <T, K> Stream<java.lang.Iterable<T>>.groupBy(toKey: (T) -> K): Stream<Map<K, List<T>>> {
    return this.map<Map<K, List<T>>> {
        it.groupBy<T,K>(toKey)
    }
}
