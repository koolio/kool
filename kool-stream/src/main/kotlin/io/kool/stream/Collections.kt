package io.kool.stream

import java.util.concurrent.Executor
import io.kool.stream.support.*

/**
* Converts a collection into an event stream
*/
fun <T> Iterable<T>.toStream(executor: Executor = SynchronousExecutor()): Stream<T> = StreamCollection(this, executor)
