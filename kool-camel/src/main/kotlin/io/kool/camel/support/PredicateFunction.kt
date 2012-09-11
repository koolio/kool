package io.kool.camel.support

import org.apache.camel.Exchange
import org.apache.camel.Predicate

/**
* An implementation of [[Predicate]] which takes a function
*/
public class PredicateFunction(val fn: Exchange.() -> Boolean): Predicate {
//public class PredicateFunction(val fn: (Exchange) -> Boolean): Predicate {
    public override fun matches(exchange: Exchange?): Boolean {
        return if (exchange != null) {
            exchange.fn()
            //fn(exchange)
        } else false
    }

    public override fun toString(): String {
        return "PredicateFunction($fn)"
    }
}