package io.kool.stream

import java.util.concurrent.Executor
import io.kool.stream.support.*

/**
* Converts a collection into an event stream
*/
fun <T> java.lang.Iterable<T>.toStream(executor: Executor = SynchronousExecutor()): Stream<T> = StreamCollection(this, executor)
