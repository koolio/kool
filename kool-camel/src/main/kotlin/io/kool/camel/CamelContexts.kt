package io.kool.camel

import io.kool.stream.*
import io.kool.stream.support.*
import io.kool.camel.support.*
import org.apache.camel.CamelContext
import org.apache.camel.Endpoint
import org.apache.camel.util.CamelContextHelper
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.model.RouteDefinition
import org.apache.camel.model.RoutesDefinition
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.ProducerTemplate
import org.apache.camel.ConsumerTemplate

/**
 * Helper method to create a new [[ModelCamelContext]]
 */
inline fun <T> camel(useBlock: ModelCamelContext.() -> T): T {
    val context = createCamelContext()
    return context.use(useBlock)
}

/**
 * Helper method to create a new [[ModelCamelContext]]
 */
inline fun createCamelContext(): ModelCamelContext {
    return DefaultCamelContext()
}

/**
 * Looks up the given endpoint in the [[CamelContext]] throwing an exception if its not available
 */
inline fun CamelContext.endpoint(uri: String): Endpoint = CamelContextHelper.getMandatoryEndpoint(this, uri)!!

// TODO if http://youtrack.jetbrains.com/issue/KT-1751 is resolved we can omit the
// verbose klass parameter

/**
 * Looks up the given endpoint of type T in the [[CamelContext]] throwing an exception if its not available
 */
inline fun <T: Endpoint> CamelContext.endpoint(uri: String, klass: Class<T>): T = CamelContextHelper.getMandatoryEndpoint(this, uri, klass)!!

/**
 * Looks up the given [[MockEndpoint]] in the [[CamelContext]] throwing an exception if its not available
 */
inline fun CamelContext.mockEndpoint(uri: String): MockEndpoint = CamelContextHelper.getMandatoryEndpoint(this, uri, javaClass<MockEndpoint>())!!

/**
 * Creates a [[ProducerTemplate]] on this context
 */
inline fun CamelContext.producerTemplate(): ProducerTemplate = createProducerTemplate()!!

/**
 * Creates a [[ProducerTemplate]] on this context
 */
inline fun CamelContext.consumerTemplate(): ConsumerTemplate = createConsumerTemplate()!!

/**
 * Starts the given [[CamelContext]], processes the block and then stops it at the end
 */
inline fun <T> ModelCamelContext.use(block: ModelCamelContext.() -> T): T {
    var closed = false
    try {
        this.start()
        return this.block()
    } catch (e: Exception) {
        closed = true
        try {
            this.stop()
        } catch (closeException: Exception) {
            // eat the closeException as we are already throwing the original cause
            // and we don't want to mask the real exception

            // TODO on Java 7 we should call
            // e.addSuppressed(closeException)
            // to work like try-with-resources
            // http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html#suppressed-exceptions
        }
        throw e
    } finally {
        if (!closed) {
            this.stop()
        }
    }
}


/**
 * A builder to add some route builders to the [[ModelCamelContext]]
 */
inline fun CamelContext.routes(init: RoutesDefinition.() -> Any): RoutesDefinition {
    val definition = RoutesDefinition()
    definition.init()
    addRouteDefinitions(definition.getRoutes())
    return definition
}

/**
 * A builder to add some route builders to the [[ModelCamelContext]]
 */
/*
//inline fun ModelCamelContext.routes(init: RoutesDefinition.() -> Any): RoutesDefinition {
inline fun CamelContext.route(init: CamelBuilder.() -> Any): RouteDefinition {
    val definition = RoutesDefinition()
    val route = definition.route()!!
    val builder = CamelBuilder(route)
    builder.init()
    addRouteDefinitions(definition.getRoutes())
    return route
}
*/