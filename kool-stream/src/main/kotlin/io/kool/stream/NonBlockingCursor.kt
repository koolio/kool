package io.kool.stream

/**
 * A [[Cursor]] which can woken up by a [[NonBlockingHandler]]
 * to resume event delivery.
 */
public trait NonBlockingCursor : Cursor {

    /**
     * Wakes up the cursor so that it continues to raise events.  A cursor usually
     * goes into a sleep state if the NonBlockingHandler cannot accept another event.
     */
    public fun wakeup(): Unit
}