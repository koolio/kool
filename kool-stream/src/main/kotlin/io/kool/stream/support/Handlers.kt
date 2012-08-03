package io.kool.stream.support

import io.kool.stream.*
import java.io.Closeable
import java.util.ArrayDeque
import java.util.Queue
import java.util.concurrent.atomic.AtomicBoolean

/**
* Useful base class for [[Handler]] to avoid having to implement [[onComplete()]] or [[onError()]]
*/
abstract class AbstractHandler<T> : Handler<T>(), Closeable {
    private val closedFlag = AtomicBoolean(false)

    public override fun close() {
        if (closedFlag.compareAndSet(false, true)) {
            cursor?.close()
            doClose()
        }
    }

    /**
     * Returns true if this object is closed
     */
    public fun isClosed(): Boolean = closedFlag.get()


    var cursor: Cursor? = null

    public override fun onOpen(cursor: Cursor) {
        $cursor = cursor
    }

    public override fun onComplete() {
        close()
    }

    public override fun onError(e: Throwable) {
        close()
    }

    /**
     * Implementations can override this to implement custom closing logic
     */
    protected open fun doClose(): Unit {
    }
}

/**
 * Allows a function to be converted into an [[Handler]] so we can use a simple function to consume events
 */
class FunctionHandler<T>(val fn: (T) -> Unit): AbstractHandler<T>() {

    public override fun onNext(next: T) {
        (fn)(next)
    }
}

/**
 * A [[Handler]] where the next value may be consumed by the given *offer* function if it returns true
 * but if not its added to a retry buffer for next time.
 */
class OfferHandler<T>(val offer: (T) -> Boolean): AbstractHandler<T>() {
    val buffer: Queue<T> = ArrayDeque<T>()

    public override fun onNext(next: T) {
        while (buffer.notEmpty()) {
            val head = buffer.peek()
            if (head == null || (offer)(head)) {
                buffer.remove()
            } else {
                buffer.add(next)
                return
            }
        }
        val result = (offer)(next)
        if (!result) {
            buffer.add(next)
        }
    }
}

/**
 * A [[Handler]] which filters elements in the stream
 */
class FilterHandler<T>(delegate: Handler<T>, val predicate: (T) -> Boolean): DelegateHandler<T,T>(delegate) {

    public override fun onNext(next: T) {
        if ((predicate)(next)) {
            delegate.onNext(next)
        }
    }
}

/**
 * A [[Handler]] which processes elements in the stream with a function first before delegating to the underlying handler
 */
class ForEachHandler<T>(delegate: Handler<T>, val fn: (T) -> Unit): DelegateHandler<T,T>(delegate) {

    public override fun onNext(next: T) {
        (fn)(next)
        delegate.onNext(next)
    }
}
