package io.kool.stream.support

import io.kool.stream.*
import java.io.Closeable
import java.util.concurrent.Executor
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ScheduledFuture


/**
 * A [[Stream]] which uses a [[Timer]] to schedule the invocation of the [[Handler]] at specific
 * scheduled times defined by the *schedularFunction*
 */
class TimerStream(val schedularFunction: (TimerTask) -> Unit): Stream<Long>() {
    fun toString() = "TimerStream($schedularFunction)"

    public override fun open(handler: Handler<Long>): Cursor {
        val task = handler.toTimerTask()
        val cursor = TimerTaskCursor(task, handler)
        handler.onOpen(cursor)
        (schedularFunction)(task)
        return cursor
    }
}

/**
 * A [[Cursor]] for closing an underlying [[TimerTask]]
 */
public class TimerTaskCursor(val task: TimerTask, val handler: Handler<*>) : AbstractCursor() {

    public override fun doClose() {
        task.cancel()
        handler.onComplete()
    }
}