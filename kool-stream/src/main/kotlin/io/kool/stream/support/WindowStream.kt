package io.kool.stream.support

import io.kool.stream.*
import java.util.ArrayDeque
import java.util.ArrayList
import java.util.Queue

/**
* Creates an [[Stream]] which puts each event into a [[List]] of a fixed size
*/
class WindowStream<T>(val delegate: Stream<T>, val size: Int) : Stream<List<T>>() {

    public override fun open(handler: Handler<List<T>>): Cursor {
        val newHandler: Handler<T> = WindowHandler(handler, size)
        val cursor = delegate.open(newHandler)
        newHandler.onOpen(cursor)
        return cursor
    }
}


/**
* Creates an [[Stream]] which puts the events into a moving window
*/
class WindowHandler<T>(delegate: Handler<List<T>>, val size: Int): DelegateHandler<T,List<T>>(delegate) {
    val queue: Queue<T> = ArrayDeque<T>(size)

    public override fun onNext(next: T) {
        while (queue.size() >= size) {
            queue.remove()
        }
        queue.add(next)

        // now lets create an immutable copy to avoid any concurrent issues
        // TODO we may wish to use a more optimal fixed ReadOnlyList here
        // TODO change to queue.toImmutableList() when its available
        val copy = queue.toCollection(ArrayList<T>(size))
        delegate.onNext(copy)
    }
}
