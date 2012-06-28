package io.kool.stream.support

import io.kool.stream.*

import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A useful base class for implementing a [[Cursor]]
 */
abstract class AbstractCursor: Cursor {
    private val closedFlag = AtomicBoolean(false)

    public override fun close() {
        if (closedFlag.compareAndSet(false, true)) {
            doClose()
        }
    }

    /**
     * Returns true if this object is closed
     */
    public override fun isClosed(): Boolean = closedFlag.get()


    /**
     * Implementations must implement this method to perform the actual close logic
     */
    protected abstract fun doClose()
}