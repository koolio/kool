package io.kool.camel.support

import org.apache.camel.Consumer
import org.apache.camel.Endpoint
import org.apache.camel.Exchange
import org.apache.camel.Processor as ExchangeProcessor
import io.kool.stream.Cursor
import io.kool.stream.Handler
import io.kool.stream.Stream
import io.kool.stream.support.*
import org.apache.camel.Producer

/**
 * A [[Stream]] which consumes messages on a Camel [[Endpoint]]
 */
public class EndpointStream<T>(val endpoint: Endpoint,val fn: (Exchange) -> T): Stream<T>() {
    public fun toString(): String = "EndpointStream($endpoint)"

    public override fun open(handler: Handler<T>): Cursor {
        val processor = HandlerProcessor(handler, fn)
        val consumer = endpoint.createConsumer(processor)!!
        val cursor = ConsumerCursor(consumer, handler)
        handler.onOpen(cursor)
        consumer.start()
        return cursor
    }
}

public class HandlerProcessor<T>(val handler: Handler<T>, val fn: (Exchange) -> T): ExchangeProcessor {
    public override fun toString() = "HandlerProcessor($handler)"

    public override fun process(exchange: Exchange?): Unit {
        if (exchange != null) {
            val message = (fn)(exchange)
            if (message != null) {
                handler.onNext(message)
            }

        }
    }
}

/**
 * Represents a [[Cursor]] on a Camel [[Consumer]]
 *
 * The consumer is registered after this object is constructed
 * so that we can pass the cursor into the [[Handler]]'s Open before
 * the consumer is created to avoid it arriving after a Next
 */
public class ConsumerCursor(val consumer: Consumer, val handler: Handler<*>): AbstractCursor() {
    public override fun toString() = "ConsumerCursor($consumer, $handler)"

    public override fun doClose() {
        consumer.stop()
        handler.onComplete()
    }
}

/**
 * A [[Handler]] implementation which sends messages to a Camel [[Producer]]
 */
public class ProducerHandler<T>(val producer: Producer) : AbstractHandler<T>() {
    public override fun toString() = "EndpointProducerHandler($producer)"

    public override fun onOpen(cursor: Cursor) {
        super.onOpen(cursor)
        producer.start()
    }

    public override fun onNext(next: T) {
        val exchange = producer.createExchange()!!
        val message = exchange.getIn()!!
        message.setBody(next)
        producer.process(exchange)
    }

    // TODO compiler bug this should be protected!!!
    public override fun doClose() {
        super.doClose()
        producer.stop()
    }
}