package io.kool.stream.support

import io.kool.stream.*

/**
* Creates an [[Stream]] which transforms the handler using the given function
*/
class DelegateStream<T>(val delegate: Stream<T>, val fn: (Handler<T>) -> Handler<T>) : Stream<T>() {

    public override fun open(handler: Handler<T>): Cursor {
        val newHandler = (fn)(handler)
        return openDelegate(delegate, newHandler)
    }
}

/**
 * Useful base class which delegates to another [[Handler]]
 */
abstract class DelegateHandler<T,R>(val delegate: Handler<R>) : Handler<T>() {

    public override fun onOpen(cursor: Cursor) {
        delegate.onOpen(cursor)
    }

    public override fun onComplete() {
        delegate.onComplete()
    }

    public override fun onError(e: Throwable) {
        delegate.onError(e)
    }
}
