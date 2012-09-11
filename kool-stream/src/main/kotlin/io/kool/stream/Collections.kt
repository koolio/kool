package io.kool.stream

import io.kool.stream.support.*
import java.util.concurrent.Executor

/**
* Converts a collection into an event stream
*/
fun <T> Iterable<T>.toStream(executor: Executor = SynchronousExecutor()): Stream<T> = StreamCollection(this, executor)
