package io.kool.stream.support

import io.kool.stream.*
import java.io.Closeable
import java.util.TimerTask
import java.util.concurrent.Future


public open class DefaultCursor(): AbstractCursor() {
    protected override fun doClose() {
    }
}

public class CompositeCursor(val cursors: List<Cursor>): AbstractCursor() {
    protected override fun doClose() {
        for (cursor in cursors) {
            cursor.close()
        }
    }
}

/**
 * A task which iterates over an iterator invoking the [[Handler]]
 * until its complete
 */
public open class IteratorTask<T>(val iter: Iterator<T>, val handler: Handler<T>): DefaultCursor(), Runnable {
    public override fun run(): Unit {
        try {
            for (element in iter) {
                if (element != null) {
                    handler.onNext(element)
                    if (isClosed()) break
                }
            }
            handler.onComplete()
            close()
        } catch (e: Throwable) {
            handler.onError(e)
            close()
        }
    }
}

