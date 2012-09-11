package io.kool.camel

import io.kool.camel.support.*
import io.kool.stream.*
import org.apache.camel.Endpoint
import org.apache.camel.Exchange
import org.apache.camel.Message

/**
* Creates a stream of [[Message]] objects by consuming from the given [[Endpoint]] URI
*/
inline fun Endpoint.toStream(): Stream<Message> {
    return toStream{ it.getIn()!! }
}

/**
 * Creates a stream of events by consuming messages from the given [[Endpoint]] URI and applying the given function on each Exchange
 */
inline fun <T> Endpoint.toStream(fn: (Exchange) -> T): Stream<T> {
    return EndpointStream(this, fn)
}

/**
 * Creates a stream of [[Exchange]] objects by consuming from the given [[Endpoint]] URI
 */
inline fun Endpoint.toExchangeStream(): Stream<Exchange> {
    return toStream{ it }
}

/**
 * Creates a stream of events by consuming messages from the given [[Endpoint]] of the given type
 */
// TODO is there a way to avoid explicit passing in of the class?
inline fun <T> Endpoint.toStreamOf(klass: Class<T>): Stream<T> {
    return if (klass.isAssignableFrom(javaClass<Exchange>())) {
        EndpointStream(this) {
            it as T
        }
    } else {
        EndpointStream(this) {
            it.getIn<T>(klass) as T
        }
    }
}

/**
 * Sends events on a [[Stream]] to an endpoint
 */
inline fun <T> Stream<T>.sendTo(endpoint: Endpoint): Cursor {
    val producer = endpoint.createProducer()!!
    val handler = ProducerHandler<T>(producer)
    return open(handler)
}