package io.kool.stream


/**
 * Handles asynchronous events from a stream where flow control may be required if the handler cannot process the next event.
 *
 * A [[Stream]] will invoke only one of these methods at a time from a single thread, so the handler does
 * not have to worry about concurrent access.
 *
 * The sequence of events is onOpen, offerNext*, (onComplete|onError) so that there will always be an onOpen first
 * then zero to many offerNext events and finally either onComplete or onError.
 */
public abstract class NonBlockingHandler<in T> {

    /**
     * Receives the [[NonBlockingCursorCursor]] when the stream is opened in case
     * the handler wishes to close the cursor or if it needs to wake up the cursor
     * at a later time.
     */
    public abstract fun onOpen(cursor: NonBlockingCursor): Unit

    /**
     * Receives the next value of a stream and attempts to process it, returning *true* if its processed
     * or *false* if it cannot be processed right now.
     *
     * If this method returns false then the cursor MAY not deliver the handler any
     * more events until you wake it up by calling the [[NonBlockingCursorCursor]]'s `wakeup()` method.
     */
    public abstract fun offerNext(next: T): Boolean

    /**
     * Marks a stream as completed
     */
    public abstract fun onComplete(): Unit

    /**
     * Marks a stream as completed with a failure
     */
    public abstract fun onError(e: Throwable): Unit
}
