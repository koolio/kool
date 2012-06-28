package io.kool.stream


/**
 * Handles asynchronous events from a stream.
 *
 * A [[Stream]] will invoke only one of these methods at a time from a single thread, so the handler does
 * not have to worry about concurrent access.
 *
 * The sequence of events is Open, Next*, (Complete|Error) so that there will always be an Open first
 * then zero to many Next events and finally either Complete or Error
 */
public abstract class Handler<in T> {

    /**
     * Receives the [[Cursor]] when the stream is opened in case
     * the handler wishes to close the cursor itself
     */
    public abstract fun onOpen(cursor: Cursor): Unit

    /**
     * Receives the next value of a stream
     */
    public abstract fun onNext(next: T): Unit

    /**
     * Marks a stream as completed
     */
    public abstract fun onComplete(): Unit

    /**
     * Marks a stream as completed with a failure
     */
    public abstract fun onError(e: Throwable): Unit
}
