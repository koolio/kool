package io.kool.stream.support

import io.kool.stream.*
import java.util.concurrent.Future
import java.util.concurrent.ScheduledFuture

/**
* A [[Cursor]] for closing an underlying [[Future]]
*/
public class FutureCursor( val handler: Handler<*>, val mayInterruptIfRunningOnClose: Boolean = true) : AbstractCursor() {

    /**
     * Allows the future to be specified after this cursor has been created so that the cursor can be passed into the
     * Open event before the Future has been created to avoid timing issues where a Future may complete before a stream
     * is opened
     */
    var future: Future<*>? = null

    public override fun doClose() {
        future?.cancel(mayInterruptIfRunningOnClose)
        handler.onComplete()
    }
}
/**
 * A [[Stream]] which uses an [[Timer]] to schedule the invocation of the [[Handler]] at specific
 * scheduled times defined by the *schedularFunction*
 */
class ScheduledFutureStream(val schedularFunction: (Runnable) -> ScheduledFuture<*>?): Stream<Long>() {
    fun toString() = "ScheduledFutureStream($schedularFunction)"

    public override fun open(handler: Handler<Long>): Cursor {
        val runnable = handler.toTimerRunnable()
        val cursor = FutureCursor(handler)
        handler.onOpen(cursor)

        // lets pass in the future to the cursor after we've
        // opened the handler to avoid any timing issues
        // where the runnable fires in Next events before the Open
        cursor.future = (schedularFunction)(runnable)
        return cursor
    }
}