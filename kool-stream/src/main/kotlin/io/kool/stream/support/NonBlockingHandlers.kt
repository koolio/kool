package io.kool.stream.support

import java.util.ArrayDeque
import java.util.Queue
import java.util.concurrent.atomic.AtomicBoolean
import io.kool.stream.*
import java.io.Closeable

/**
 * Adapts a [[NonBlockingHandler]] to work with a regular [[Stream]] which works with a [[Handler]]
 * by buffering up any events which could not be offered so they can be retried before the next event is
 * offered
 */
open class NonBlockingHandlerAdapter<T>(val delegate: NonBlockingHandler<T>): Handler<T>() {
    val buffer: Queue<T> = ArrayDeque<T>()
    var suspendableCursor: NonBlockingCursor? = null

    public override fun onOpen(cursor: Cursor) {
        val newCursor = cursor.toNonBlockingCursorCursor()
        suspendableCursor = newCursor
        delegate.onOpen(newCursor)
    }

    public override fun onComplete() {
        delegate.onComplete()
    }

    public override fun onError(e: Throwable) {
        delegate.onError(e)
    }

    public override fun onNext(next: T) {
        while (buffer.notEmpty()) {
            val head = buffer.peek()
            if (head == null || delegate.offerNext(head)) {
                buffer.remove()
            } else {
                buffer.add(next)
                onOfferFailed()
                return
            }
        }
        val result = delegate.offerNext(next)
        if (!result) {
            buffer.add(next)
            onOfferFailed()
        }
    }

    /**
     * We cannot suspend the cursor as we have no way to know
     * when to resume it again but a derived class may choose to do so
     */
    protected open fun onOfferFailed(): Unit {
    }
}

class NonBlockingCursorAdapter(val delegate: Cursor): AbstractCursor(), NonBlockingCursor {

    public override fun wakeup() {
    }

    protected override fun doClose() {
        delegate.close()
    }
}

/**
 * Useful base class for [[Handler]] to avoid having to implement [[onComplete()]] or [[onError()]]
 */
abstract class AbstractNonBlockingHandler<T> : NonBlockingHandler<T>(), Closeable {
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


    var cursor: NonBlockingCursor? = null

    public override fun onOpen(cursor: NonBlockingCursor) {
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
 * Allows a function to be converted into an [[NonBlockingHandler]] so we can use a simple function to consume events
 */
class FunctionNonBlockingHandler<T>(val fn: (T) -> Boolean) : AbstractNonBlockingHandler<T>() {
    public override fun offerNext(next: T): Boolean {
        return (fn)(next)
    }
}