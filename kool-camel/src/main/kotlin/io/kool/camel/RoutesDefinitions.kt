package io.kool.camel

import io.kool.camel.support.*
import java.util.Comparator
import org.apache.camel.Endpoint
import org.apache.camel.Exchange
import org.apache.camel.model.*
import org.apache.camel.processor.aggregate.AggregationStrategy
import org.apache.camel.spi.IdempotentRepository

/**
* A builder API to help create a new [[RouteDefinition]] on a [[CamelContext]]
* using Camel's Java DSL
*/
inline fun RoutesDefinition.route(init: RouteDefinition.() -> Any): RouteDefinition {
    val definition = this.route()!!
    definition.init()
    return definition
}

/**
 * A builder API to help create a new [[RouteDefinition]] on a [[CamelContext]]
 * using Camel's Java DSL
 */
inline fun RoutesDefinition.from(uri: String, init: RouteDefinition.() -> Any): RouteDefinition {
    val definition = this.route()!!
    definition.from(uri)
    definition.init()
    return definition
}


/**
 * Sends the message to the given URI
 */
inline fun <T: ProcessorDefinition<T>> T.sendTo(uri: String): T {
    return this.to(uri)!!
}

/**
 * Sends the message to the given endpoint
 */
inline fun <T: ProcessorDefinition<T>> T.sendTo(endpoint: Endpoint): T {
    return this.to(endpoint)!!
}

// TODO the sendTo methods on FilterDefinition should not really be required
// they are workarounds from compiler glitches

/**
 * Sends the message to the given URI
 */
inline fun FilterDefinition.sendTo(uri: String): FilterDefinition {
    to(uri)
    return this
}

/**
 * Sends the message to the given endpoint
 */
inline fun FilterDefinition.sendTo(endpoint: Endpoint): FilterDefinition {
    to(endpoint)
    return this
}
/**
 * Performs a filter using the function block as the predicate
 */
inline fun <T: ProcessorDefinition<T>> T.filter(predicate: Exchange.() -> Boolean, block: FilterDefinition.() -> Any): T {
    val predicateInstance = PredicateFunction(predicate)
    val filterBlock: FilterDefinition = filter(predicateInstance)!!
    filterBlock.block()
    return this
}

/**
 * Performs a filter using the function block as the predicate, then calling *then* on the result object will
 * pass the action block.
 */
inline fun <T: ProcessorDefinition<T>> T.filter(predicate: Exchange.() -> Boolean): ThenDefinition<T> {
    val predicateInstance = PredicateFunction(predicate)
    val filterBlock = filter(predicateInstance)!!
    return ThenDefinition<T>(filterBlock)
}

/**
 * Performs a content based router
 */
inline fun <T: ProcessorDefinition<T>> T.choice(block: ChoiceDefinition.() -> Any?): T {
    val choiceBlock = choice()!!
    choiceBlock.block()
    return this
}

/**
 * Evaluates the nested body if this predicate matches
 */
inline fun ChoiceDefinition.filter(predicate: Exchange.() -> Boolean): ThenDefinition<ChoiceDefinition> {
    val predicateInstance = PredicateFunction(predicate)
    this.`when`(predicateInstance)

    // lets get the last when output
    val outputs: List<Any> = this.getOutputs()!! as List<Any>
    val whenDefinition = outputs.last
    if (whenDefinition is WhenDefinition) {
        return ThenDefinition<ChoiceDefinition>(whenDefinition)
    } else {
        throw IllegalStateException("Could not find WhenDefinition in ChoiceDefinition, found " + whenDefinition)
    }
}

/**
 * Adds an otherwise section
 */
inline fun ChoiceDefinition.otherwise(block: ChoiceDefinition.() -> Any?): ChoiceDefinition {
    this.otherwise()
    this.block()
    end()

    // TODO should we be returning the parent node?
    return this
}


/**
 * Processes the exchange using a function block
 */
inline fun <T: ProcessorDefinition<T>> T.process(block: Exchange.() -> Unit): T {
    val processor = FunctionProcessor(block)
    this.process(processor)
    return this
}


/**
 * Transforms the message, setting the output body to the result of the function block
 */
inline fun <T: ProcessorDefinition<T>> T.transform(block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.transform(expression)
    return this
}

/**
 *  <a href="http://camel.apache.org/idempotent-consumer.html">Idempotent consumer EIP:</a>
 * Creates an {@link org.apache.camel.processor.idempotent.IdempotentConsumer IdempotentConsumer}
 * to avoid duplicate messages
 */
inline fun <T: ProcessorDefinition<T>> T.idempotentConsumer(block: Exchange.() -> Any?): IdempotentConsumerDefinition {
    val expression = FunctionExpression(block)
    return this.idempotentConsumer(expression)!!
}


/**
 * <a href="http://camel.apache.org/idempotent-consumer.html">Idempotent consumer EIP:</a>
 * Creates an {@link org.apache.camel.processor.idempotent.IdempotentConsumer IdempotentConsumer}
 * to avoid duplicate messages
 *
 * @param messageIdExpression  expression to test of duplicate messages
 * @param idempotentRepository  the repository to use for duplicate check
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.idempotentConsumer(idempotentRepository: IdempotentRepository<*>, block: Exchange.() -> Any?): IdempotentConsumerDefinition {
    val expression = FunctionExpression(block)
    return this.idempotentConsumer(expression, idempotentRepository)!!
}


/**
 * Creates a validation expression which only if it is <tt>true</tt> then the
 * exchange is forwarded to the destination.
 * Otherwise a {@link org.apache.camel.processor.validation.PredicateValidationException} is thrown.
 *
 * @param expression  the expression
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.idempotentConsumer(block: Exchange.() -> Any?): ValidateDefinition {
    val expression = FunctionExpression(block)
    return this.validate(expression)!!
}

/**
 * <a href="http://camel.apache.org/recipient-list.html">Recipient List EIP:</a>
 * Creates a dynamic recipient list allowing you to route messages to a number of dynamically specified recipients
 *
 * @param recipients expression to decide the destinations
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.recipientList(block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.recipientList(expression)!!
    // TODO should we return RecipientListDefinition
    return this
}

/**
 * <a href="http://camel.apache.org/recipient-list.html">Recipient List EIP:</a>
 * Creates a dynamic recipient list allowing you to route messages to a number of dynamically specified recipients
 *
 * @param recipients expression to decide the destinations
 * @param delimiter  a custom delimiter to use
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.recipientList(delimiter: String, block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.recipientList(expression, delimiter)!!
    // TODO should we return RecipientListDefinition
    return this
}


/**
 * <a href="http://camel.apache.org/routing-slip.html">Routing Slip EIP:</a>
 * Creates a routing slip allowing you to route a message consecutively through a series of processing
 * steps where the sequence of steps is not known at design time and can vary for each message.
 * <p/>
 * The route slip will be evaluated <i>once</i>, use {@link #dynamicRouter()} if you need even more dynamic routing.
 *
 * @param expression  to decide the destinations
 * @param uriDelimiter  is the delimiter that will be used to split up
 *                      the list of URIs in the routing slip.
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.routingSlip(uriDelimiter: String, block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.routingSlip(expression, uriDelimiter)!!
    // TODO should we return RoutingSlipDefinition
    return this
}

/**
 * <a href="http://camel.apache.org/routing-slip.html">Routing Slip EIP:</a>
 * Creates a routing slip allowing you to route a message consecutively through a series of processing
 * steps where the sequence of steps is not known at design time and can vary for each message.
 * <p/>
 * The list of URIs will be split based on the default delimiter {@link RoutingSlipDefinition#DEFAULT_DELIMITER}
 * <p/>
 * The route slip will be evaluated <i>once</i>, use {@link #dynamicRouter()} if you need even more dynamic routing.
 *
 * @param expression  to decide the destinations
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.routingSlip(block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.routingSlip(expression)!!
    // TODO should we return RoutingSlipDefinition
    return this
}


/**
 * <a href="http://camel.apache.org/dynamic-router.html">Dynamic Router EIP:</a>
 * Creates a dynamic router allowing you to route a message consecutively through a series of processing
 * steps where the sequence of steps is not known at design time and can vary for each message.
 * <p/>
 * <br/><b>Important:</b> The expression will be invoked repeatedly until it returns <tt>null</tt>, so be sure it does that,
 * otherwise it will be invoked endlessly.
 *
 * @param expression  to decide the destinations, which will be invoked repeatedly
 *                    until it evaluates <tt>null</tt> to indicate no more destinations.
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.dynamicRouter(block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.dynamicRouter(expression)!!
    // TODO should we return DynamicRouterDefinition
    return this
}

/**
 * <a href="http://camel.apache.org/splitter.html">Splitter EIP:</a>
 * Creates a splitter allowing you split a message into a number of pieces and process them individually.
 * <p>
 * This splitter responds with the original input message. You can use a custom {@link AggregationStrategy} to
 * control what to respond from the splitter.
 *
 * @param block  the expression on which to split the message
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.split(block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.split(expression)!!
    // TODO should we return SplitDefinition
    return this
}

/**
 * <a href="http://camel.apache.org/splitter.html">Splitter EIP:</a>
 * Creates a splitter allowing you split a message into a number of pieces and process them individually.
 * <p>
 * The splitter responds with the answer produced by the given {@link AggregationStrategy}.
 *
 * @param expression  the expression on which to split
 * @param aggregationStrategy  the strategy used to aggregate responses for every part
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.split(aggregationStrategy: AggregationStrategy, block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.split(expression, aggregationStrategy)!!
    // TODO should we return SplitDefinition
    return this
}

/**
 * <a href="http://camel.apache.org/resequencer.html">Resequencer EIP:</a>
 * Creates a resequencer allowing you to reorganize messages based on some comparator.
 *
 * @param expression the expression on which to compare messages in order
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.resequence(block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.resequence(expression)!!
    // TODO should we return ResequenceDefinition
    return this
}

/**
 * <a href="http://camel.apache.org/aggregator.html">Aggregator EIP:</a>
 * Creates an aggregator allowing you to combine a number of messages together into a single message.
 *
 * @param correlationExpression the expression used to calculate the
 *                              correlation key. For a JMS message this could be the
 *                              expression <code>header("JMSDestination")</code> or
 *                              <code>header("JMSCorrelationID")</code>
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.aggregate(block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.aggregate(expression)!!
    // TODO should we return AggregateDefinition
    return this
}

/**
 * <a href="http://camel.apache.org/aggregator.html">Aggregator EIP:</a>
 * Creates an aggregator allowing you to combine a number of messages together into a single message.
 *
 * @param correlationExpression the expression used to calculate the
 *                              correlation key. For a JMS message this could be the
 *                              expression <code>header("JMSDestination")</code> or
 *                              <code>header("JMSCorrelationID")</code>
 * @param aggregationStrategy the strategy used for the aggregation
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.aggregate(aggregationStrategy: AggregationStrategy, block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.aggregate(expression, aggregationStrategy)!!
    // TODO should we return AggregateDefinition
    return this
}

/**
 * <a href="http://camel.apache.org/delayer.html">Delayer EIP:</a>
 * Creates a delayer allowing you to delay the delivery of messages to some destination.
 *
 * @param delay  an expression to calculate the delay time in millis
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.delay(block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.delay(expression)!!
    // TODO should we return DelayDefinition
    return this
}

/**
 * <a href="http://camel.apache.org/throttler.html">Throttler EIP:</a>
 * Creates a throttler allowing you to ensure that a specific endpoint does not get overloaded,
 * or that we don't exceed an agreed SLA with some external service.
 * <p/>
 * Will default use a time period of 1 second, so setting the maximumRequestCount to eg 10
 * will default ensure at most 10 messages per second.
 *
 * @param maximumRequestCount  an expression to calculate the maximum request count
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.throttle(block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.throttle(expression)!!
    // TODO should we return ThrottleDefinition
    return this
}

/**
 * <a href="http://camel.apache.org/loop.html">Loop EIP:</a>
 * Creates a loop allowing to process the a message a number of times and possibly process them
 * in a different way. Useful mostly for testing.
 *
 * @param expression the loop expression
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.loop(block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.loop(expression)!!
    // TODO should we return LoopDefinition
    return this
}

/**
 * <a href="http://camel.apache.org/message-translator.html">Message Translator EIP:</a>
 * Adds a processor which sets the body on the IN message
 *
 * @param expression   the expression used to set the body
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.setBody(block: Exchange.() -> Any?): T = transform(block)

/**
 * Adds a processor which sets the body on the FAULT message
 *
 * @param expression   the expression used to set the body
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.setFaultBody(block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.setFaultBody(expression)!!
    return this
}

/**
 * Adds a processor which sets the header on the IN message
 *
 * @param name  the header name
 * @param expression  the expression used to set the header
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.setHeader(name: String, block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.setHeader(name, expression)!!
    return this
}


/**
 * Sorts the expression using a default sorting based on toString representation.
 *
 * @param expression  the expression, must be convertable to {@link List}
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.sort(block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.sort(expression)!!
    return this
}

/**
 * Sorts the expression using the given comparator
 *
 * @param expression  the expression, must be convertable to {@link List}
 * @param comparator  the comparator to use for sorting
 * @return the builder
 */
inline fun <T: ProcessorDefinition<T>> T.sort(comparator: Comparator<*>, block: Exchange.() -> Any?): T {
    val expression = FunctionExpression(block)
    this.sort(expression, comparator)!!
    return this
}
