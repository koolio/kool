package io.kool.stream.support

import io.kool.stream.*
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.*

/**
* Creates an [[Stream]] which emits a tuple of [[#(A,B)]] when *stream1* has had an event and *stream2* has had an event
*/
abstract class JoinStream<A,B,T>(val streamA: Stream<A>, val streamB: Stream<B>): Stream<T>() {

    public override fun open(handler: Handler<T>): Cursor {
        val newHandler = createHandler(handler)
        val cursor = newHandler.open()
        newHandler.onOpen(cursor)
        return cursor
    }

    protected abstract fun createHandler(handler: Handler<T>): JoinHandlerSupport<A,B,T>
}


/**
 * A useful base class for combining two [[Stream]] instances together into a new stream
 */
abstract class JoinHandlerSupport<A,B,T>(val streamA: Stream<A>, val streamB: Stream<B>, delegate: Handler<T>): DelegateHandler<T,T>(delegate) {
    val lock = ReentrantLock()

    val handlerA = FunctionHandler<A>{
        onNextA(it)
    }

    val handlerB = FunctionHandler<B>{
        onNextB(it)
    }

    public override fun onNext(next: T) {
        // Note we synchronize access to the delegate here
        // to ensure we don't introduce any concurrent issues
        // as this could be invoked from streamA or streamB's handlers
        lock.withLock {
            delegate.onNext(next)
        }
    }

    /**
     * Processes the incoming event on [[streamA]]
     */
    protected abstract fun onNextA(a: A): Unit

    /**
     * Processes the incoming event on [[streamB]]
     */
    protected abstract fun onNextB(b: B): Unit

    /**
     * Open on the underying streams and return a composite cursor
     */
    public fun open(): Cursor {
        val cursorA = streamA.open(handlerA)
        val cursorB = streamB.open(handlerB)
        return CompositeCursor(arrayList(cursorA, cursorB))
    }
}

/**
 * Creates an [[Stream]] which emits events of type [[#(A,B)]] when *stream1* has had an event and *stream2* has had an event
 */
class FollowedByStream<A,B>(streamA: Stream<A>, streamB: Stream<B>) : JoinStream<A,B, Pair<A, B>>(streamA, streamB) {

    protected override fun createHandler(handler: Handler<Pair<A, B>>): JoinHandlerSupport<A, B, Pair<A, B>> {
        return FollowedByHandler<A,B>(streamA, streamB, handler)
    }
}

/**
 * Creates an [[Stream]] of an events of type [[#(A,B)]] when an event occurs on *streamA* followed by an event on *streamB*.
 * We filter out consecutive events on *streamA* or events on *streamB* before there is an event on *streamA*.
 */
open class FollowedByHandler<A,B>(streamA: Stream<A>, streamB: Stream<B>, delegate: Handler<Pair<A, B>>): JoinHandlerSupport<A, B, Pair<A, B>>(streamA, streamB, delegate) {
    open var valueA: A? = null
    open var valueB: B? = null

    protected override fun onNextA(a: A): Unit {
        valueA = a
        checkIfComplete()
    }

    protected override fun onNextB(b: B): Unit {
        valueB = b
        checkIfComplete()
    }

    protected open fun checkIfComplete(): Unit {
        lock.withLock {
            val a = valueA
            val b = valueB
            if (a != null && b != null) {
                valueA = null
                valueB = null
                val next: Pair<A, B> = Pair(a!!, b!!)
                onNext(next)
            }
        }
    }

    // TODO compiler bug - must have this redundant method here for some reason
    public override fun onNext(next: Pair<A, B>) {
        super.onNext(next)
    }
}

/**
 * Creates an [[Stream]] which emits events of type [[#(A?,B?)]] when either *stream1* or *stream2* raises an event
 */
class MergeStream<A,B>(streamA: Stream<A>, streamB: Stream<B>) : JoinStream<A,B, Pair<A?, B?>>(streamA, streamB) {

    protected override fun createHandler(handler: Handler<Pair<A?, B?>>): JoinHandlerSupport<A, B, Pair<A?, B?>> {
        return MergeHandler<A,B>(streamA, streamB, handler)
    }
}

/**
 * Creates an [[Stream]] of an events of type [[#(A,B)]] when an event occurs on *streamA* followed by an event on *streamB*.
 * We filter out consecutive events on *streamA* or events on *streamB* before there is an event on *streamA*.
 */
open class MergeHandler<A,B>(streamA: Stream<A>, streamB: Stream<B>, delegate: Handler<Pair<A?, B?>>): JoinHandlerSupport<A, B, Pair<A?, B?>>(streamA, streamB, delegate) {
    protected override fun onNextA(a: A): Unit {
        onNext(Pair(a, null))
    }

    protected override fun onNextB(b: B): Unit {
        onNext(Pair(null, b))
    }

    // TODO compiler bug - must have this redundant method here for some reason
    public override fun onNext(next: Pair<A?, B?>) {
        super.onNext(next)
    }
}

/**
 * Creates an [[Stream]] which emits events of type [[#(A,B)]] when either *stream1* or *stream2* raises an event, sending the previous value
 * of the other ovent. i.e. so each event on *stream1* will be processed twice if the events alternate between streams.
 */
class AndStream<A,B>(streamA: Stream<A>, streamB: Stream<B>) : JoinStream<A,B, Pair<A, B>>(streamA, streamB) {

    protected override fun createHandler(handler: Handler<Pair<A, B>>): JoinHandlerSupport<A, B, Pair<A, B>> {
        return AndHandler<A,B>(streamA, streamB, handler)
    }
}

open class AndHandler<A,B>(streamA: Stream<A>, streamB: Stream<B>, delegate: Handler<Pair<A, B>>): JoinHandlerSupport<A, B, Pair<A, B>>(streamA, streamB, delegate) {
    val lastA = AtomicReference<A?>(null)
    val lastB = AtomicReference<B?>(null)

    protected override fun onNextA(a: A): Unit {
        lastA.set(a)
        val b = lastB.get()
        if (b != null) {
            onNext(Pair(a, b!!))
        }
    }

    protected override fun onNextB(b: B): Unit {
        lastB.set(b)
        val a = lastA.get()
        if (a != null) {
            onNext(Pair(a!!, b))
        }
    }

    // TODO compiler bug - must have this redundant method here for some reason
    public override fun onNext(next: Pair<A, B>) {
        super.onNext(next)
    }
}
