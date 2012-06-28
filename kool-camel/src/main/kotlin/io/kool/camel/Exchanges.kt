package io.kool.camel

import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.util.ExchangeHelper
import org.apache.camel.CamelContext

/**
* Returns the in message body
*/
inline fun Exchange.body(): Any? {
    val message = this.getIn()
    return message?.getBody()
}

// TODO if http://youtrack.jetbrains.com/issue/KT-1751 is resolved we can omit the
// verbose klass parameter

/**
 * Returns the in message body of the given type
 */
inline fun <T> Exchange.body(klass: Class<T>): T? {
    val message = this.getIn()
    return message?.getBody<T>(klass)
}

// TODO in is a reserved word in kotlinso we can't use it as the property
var Exchange.input: Message
    get() = getIn()!!
    set(value) {
        setIn(value)
    }

var Exchange.out: Message
    get() = getOut()!!
    set(value) {
        setOut(value)
    }

/**
 * Returns the result message which is either the input or out message based on the exchange pattern
 */
val Exchange.result: Message
    get() = ExchangeHelper.getResultMessage(this)!!

/**
 * Returns the request IN [[Message]]
 */
val Exchange.request: Message
    get() = getIn()!!

/**
 * Returns the response OUT [[Message]]
 */
val Exchange.response: Message
    get() = getOut()!!

/**
 * Returns true if the given exchange pattern (if defined) can support OUT messages
 */
val Exchange.outCapable: Boolean
    get() = ExchangeHelper.isOutCapable(this)

/**
 * Returns true if the unit of work is exhausted
 */
val Exchange.unitOfWorkExhausted: Boolean
    get() = ExchangeHelper.isUnitOfWorkExhausted(this)

/**
 * Returns true if the message has been redelivered
 */
val Exchange.redelivered: Boolean
    get() = ExchangeHelper.isRedelivered(this)


/**
 * Returns true if the redelivery has been exhausted
 */
val Exchange.redeliveryExhausted: Boolean
    get() = ExchangeHelper.isRedeliveryExhausted(this)

/**
 * Returns true if the exchange has been interrupted
 */
val Exchange.interrupted: Boolean
    get() = ExchangeHelper.isInterrupted(this)


/**
 * Returns true if the exchange has a fault message
 */
val Exchange.hasFaultMessage: Boolean
    get() = ExchangeHelper.hasFaultMessage(this)

/**
 * Returns true if the exception has been handled by an error handler
 */
val Exchange.hasExceptionBeenHandledByErrorHandler: Boolean
    get() = ExchangeHelper.hasExceptionBeenHandledByErrorHandler(this)

/**
 * Returns true if the failure has been handled
 */
var Exchange.failureHanded: Boolean
    get() = ExchangeHelper.isFailureHandled(this)
    set(value) {
        setProperty(Exchange.FAILURE_HANDLED, value)
        if (value) {
        // clear exception since its failure handled
        setException(null)
        }
    }

/**
 * Returns the [[CamelContext]]
 */
val Exchange.context: CamelContext
    get() = this.getContext()!!

/**
 * Returns the MIME content type on the input message or null if one is not defined
 */
val Exchange.contentType: String?
    get() = ExchangeHelper.getContentType(this)

/**
 * Returns the MIME content encoding on the input message or null if one is not defined
 */
val Exchange.contentEncoding: String?
    get() = ExchangeHelper.getContentEncoding(this)

/**
 * Returns the input message body as a String using the empty string if its null
 */
inline fun Exchange.bodyString(nullValue: String = ""): String = body<String>(javaClass<String>()) ?: nullValue

/**
 * Provides array style access to properties on the exchange
 */
inline fun Exchange.get(propertyName: String): Any? = getProperty(propertyName)

/**
 * Provides array style access to properties on the exchange
 */
inline fun Exchange.set(propertyName: String, value: Any?): Unit = setProperty(propertyName, value)

/**
 * Creates a copy of this exchange, optionally preserving the exchange id
 */
inline fun <T> Exchange.copy(preserveExchangeId: Boolean = false): Exchange = ExchangeHelper.createCopy(this, preserveExchangeId)!!

/**
 * Copies the results from the given *source* to this exchange
 */
inline fun <T> Exchange.copyResultsFrom(source: Exchange): Unit {
    ExchangeHelper.copyResults(this, source)
}

/**
 * Copies the results from the given *source* to this exchange, preserving the pattern
 */
inline fun <T> Exchange.copyResultsPreservePatternFrom(source: Exchange): Unit {
    ExchangeHelper.copyResultsPreservePattern(this, source)
}

/**
 * Converts the given value to the given type using the current type conversion registry for the camel context
 * returning null if the conversion could not be completed
 */
inline fun <T> Exchange.convertToType(value: Any, klass: Class<T>): T? = ExchangeHelper.convertToType(this, klass, value)

/**
 * Converts the given value to the given type using the current type conversion registry for the camel context
 * throwing an exception if it could not be converted
 */
inline fun <T> Exchange.requireConvertToType(value: Any, klass: Class<T>): T = ExchangeHelper.convertToMandatoryType(this, klass, value)!!

/**
 * Creates a new instance of the given type from the injector
 */
inline fun <T> Exchange.newInstance(klass: Class<T>): T = ExchangeHelper.newInstance(this, klass)!!

/**
 * Performs a lookup in the registry of the bean name
 */
inline fun <T> Exchange.lookupBean(name: String, klass: Class<T>): T? = ExchangeHelper.lookupBean(this, name, klass)

/**
 * Performs a lookup in the registry of the bean name
 */
inline fun Exchange.lookupBean(name: String): Any? = ExchangeHelper.lookupBean(this, name)