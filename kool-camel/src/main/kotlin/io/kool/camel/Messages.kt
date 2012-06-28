package io.kool.camel

import org.apache.camel.Exchange
import org.apache.camel.Message
import java.util.Map

var Message.body: Any?
    get() = getBody()
    set(value) {
        setBody(value)
    }

var Message.headers: Map<String?, Any?>
    get() = getHeaders().orEmpty()
    set(value) {
        setHeaders(value)
    }


/**
 * Provides array style access to headers on the message
 */
inline fun Message.get(headerName: String): Any? = getHeader(headerName)

/**
 * Provides array style access to headers on the message
 */
inline fun Message.set(headerName: String, value: Any?): Unit = setHeader(headerName, value)

