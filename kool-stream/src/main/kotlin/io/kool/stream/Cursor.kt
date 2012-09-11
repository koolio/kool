package io.kool.stream

import io.kool.stream.support.NonBlockingCursorAdapter
import java.io.Closeable

/**
* Represents the processing of a [[Stream]] by a [[Handler]]
* which can be closed via the [[Closeable]] interface
*/
public trait Cursor: Closeable {
    fun isClosed(): Boolean
}

/**
 * Converts this [[Cursor]] to a [[NonBlockingCursorCursor]] if it not already
 */
inline fun Cursor.toNonBlockingCursorCursor(): NonBlockingCursor {
    return if (this is NonBlockingCursor) {
        this
    } else {
        NonBlockingCursorAdapter(this)
    }
}