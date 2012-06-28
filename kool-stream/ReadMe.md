## Kool Streams

**Kool Streams** is a simple framework for working with aynchronous events and collections. Kool Streams are inspired by a combination of the [Reactive Extensions (Rx)](http://msdn.microsoft.com/en-us/data/gg577609), [Iteratees](http://okmij.org/ftp/Streams.html) and various other similar approaches to dealing with concurrency.

### Why Kool Stream?

Kool Streams provide a form of asynchronous collection or **Event Stream** that can be [composed and processed](https://github.com/koolio/kool/blob/master/kool-stream/src/test/kotlin/test/kool/stream/CollectionTest.kt#L15) like regular [collections](http://jetbrains.github.com/kotlin/versions/snapshot/apidocs/kotlin/java/util/Collection-extensions.html) using the same kind of combinator API folks are familiar with (<a href="http://jetbrains.github.com/kotlin/versions/snapshot/apidocs/kotlin/java/util/Collection-extensions.html#filter(jet.Function1)">filter()</a>, <a href="http://jetbrains.github.com/kotlin/versions/snapshot/apidocs/kotlin/java/util/Collection-extensions.html#flatMap(jet.Function1)">flatMap()</a>, <a href="http://jetbrains.github.com/kotlin/versions/snapshot/apidocs/kotlin/java/util/Collection-extensions.html#map(jet.Function1)">map()</a>, <a href="http://jetbrains.github.com/kotlin/versions/snapshot/apidocs/kotlin/java/util/Collection-extensions.html#fold(T, jet.Function2)">fold()</a> etc) but done asynchronously to deal with time delay, network communication, to use threads efficiently and avoid blocking.

### API Overview

* [Stream<T>](http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html) represents a stream of asynchronous events. Its like an asynchronous collection, where events are pushed into a [Handler<T>](http://kool.io/versions/snapshot/apidocs/io/kool/stream/Handler.html) rather than pulled via an iterator.
* [Handler<T>](http://kool.io/versions/snapshot/apidocs/io/kool/stream/Handler.html) is used to process the events from a stream, through you can just pass a function to handle each next event instead

A [Stream<T>](http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html) is then opened via the **open()** method either passing in a handler in <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html#open(io.kool.stream.Handler)">open(Handler<T>)</a> or by passing in a function to handle even next event via <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html#open(jet.Function1)">open(fn: (T) -> Unit)</a>.

When you **open()** a [Stream<T>](http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html) you get back a [Cursor](http://kool.io/versions/snapshot/apidocs/io/kool/stream/Cursor.html) which can be used to close the stream.

### Stream contract

A [Stream<T>](http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html) must only invoke a [Handler<T>](http://kool.io/versions/snapshot/apidocs/io/kool/stream/Handler.html) from one thread at once; so a handler does not need to worry about being thread safe.

The lifecycle of events from a [Handler<T>](http://kool.io/versions/snapshot/apidocs/io/kool/stream/Handler.html) perspective is a stream will invoke

* <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Handler.html#onOpen(io.kool.stream.Cursor)">onOpen(Cursor)</a> once before any other events
* <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Handler.html#onNext(T)">onNext(T)</a> zero to many times for each event on the stream after opening
* <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Handler.html#onError(jet.Throwable)">onError(Throwable)</a> on errors
* <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Handler.html#onComplete()">onComplete()</a> when its completed

This means that a Handler can choose to close a stream if it knows it has finished processing it. For example if you can use [take(n)](https://github.com/koolio/kool/blob/master/kool-stream/src/test/kotlin/test/kool/stream/TakeTest.kt#L14) to limit the size of a stream.

### Examples

Create event streams from various things:

* [java.util.Collection](https://github.com/koolio/kool/blob/master/kool-stream/src/test/kotlin/test/kool/stream/CollectionTest.kt#L10)
* bean event listeners (TODO :)
* [java.util.Timer](https://github.com/koolio/kool/blob/master/kool-stream/src/test/kotlin/test/kool/stream/TimerTest.kt#L14) via [extension functions](http://kool.io/versions/snapshot/apidocs/io/kool/stream/java/util/Timer-extensions.html)
* [java.util.concurrent.ScheduledExecutorService](https://github.com/koolio/kool/blob/master/kool-stream/src/test/kotlin/test/kool/stream/ScheduledExecutorServiceTest.kt#L17) via [extension functions](http://kool.io/versions/snapshot/apidocs/io/kool/stream/java/util/concurrent/ScheduledExecutorService-extensions.html)
* [SimpleStream](https://github.com/koolio/kool/blob/master/kool-stream/src/test/kotlin/test/kool/stream/SimpleStreamTest.kt#L15)
* [Apache Camel Endpoints](https://github.com/koolio/kool/blob/master/kool-camel/src/test/kotlin/test/kool/camel/EndpointConsumeTest.kt#L27) via extension functions on [Endpoint](http://kool.io/versions/snapshot/apidocs/io/kool/camel/org/apache/camel/Endpoint-extensions.html)

Combine streams with Collection-style combinators

* [filter and map values](https://github.com/koolio/kool/blob/master/kool-stream/src/test/kotlin/test/kool/stream/CollectionTest.kt#L15) via <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html#filter(jet.Function1)">filter()</a> and <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html#map(jet.Function1)">map()</a>
* [filter distinct values](https://github.com/koolio/kool/blob/master/kool-stream/src/test/kotlin/test/kool/stream/DistinctTest.kt#L14) via <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html#distinct()">distinct()</a>
* [take a specific amount of events](https://github.com/koolio/kool/blob/master/kool-stream/src/test/kotlin/test/kool/stream/TakeTest.kt#L14) via <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html#take(jet.Int)">take(n)</a> or <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html#takeWhile(jet.Function1)">takeWhile(predicate)</a>

Using windows of events for *complex event processing* types of things

* [create a moving fixed window of events](https://github.com/koolio/kool/blob/master/kool-stream/src/test/kotlin/test/kool/stream/WindowTest.kt#L21) via <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html#window(jet.Int)">window(size)</a>
* [create a moving time window of events](https://github.com/koolio/kool/blob/master/kool-stream/src/test/kotlin/test/kool/stream/TimeWindowTest.kt#L22) via <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html#timeWindow(jet.Long)">timeWindow(millis)</a>
* [group events in a window](https://github.com/koolio/kool/blob/master/kool-math/src/test/kotlin/test/kool/math/GroupByTest.kt#L11) using <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/io/kool/stream/Stream-extensions.html#groupBy(jet.Function1)">groupBy(keyFunction)</a>

Finally you can pipe the events from a Stream [to any Apache Camel Endpoint](https://github.com/koolio/kool/blob/master/kool-camel/src/test/kotlin/test/kool/camel/EndpointProduceTest.kt#L33)
