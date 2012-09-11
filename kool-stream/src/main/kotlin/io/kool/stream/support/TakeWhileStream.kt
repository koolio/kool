package io.kool.stream.support

import io.kool.stream.*

/**
* Creates an [[Stream]] which takes elements from the delegate stream until the *predicate* is false
* then the stream closes the *delegate* stream.
*
* If *includeNonMatching* is true then the final value which caused the *predicate* to return false will
* also be passed to the *delegate* stream
*/
class TakeWhileStream<T>(val delegate: Stream<T>, val includeNonMatching: Boolean, val predicate: (T) -> Boolean) : Stream<T>() {

    public override fun open(handler: Handler<T>): Cursor {
        val newHandler = TakeWhileHandler(handler, includeNonMatching, predicate)
        return openDelegate(delegate, newHandler)
    }
}

/**
 * A [[Handler]] which processes elements in the stream until the predicate is false then the underlying stream is closed
 */
class TakeWhileHandler<T>(var delegate: Handler<T>, val includeNonMatching: Boolean, val predicate: (T) -> Boolean): AbstractHandler<T>() {

    public override fun onNext(next: T) {
        val matches = (predicate)(next)
        if (matches || includeNonMatching) {
            delegate.onNext(next)
        }
        if (!matches) {
            close()
        }
    }

    protected override fun doClose() {
        delegate.onComplete()
        super.doClose()
    }
}
