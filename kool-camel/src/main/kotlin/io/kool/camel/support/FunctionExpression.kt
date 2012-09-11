package io.kool.camel.support

import org.apache.camel.Exchange
import org.apache.camel.Expression
import org.apache.camel.util.ExchangeHelper

/**
* Implements [[Expression]] using a function block
*/
public class FunctionExpression(val block: Exchange.() -> Any?) : Expression {

    public override fun toString(): String? {
        return "FunctionExpression($block)"
    }

    public override fun <T> evaluate(exchange: Exchange?, klass: Class<T>?): T? {
        if (exchange != null) {
            val result = exchange.block()
            return ExchangeHelper.convertToMandatoryType(exchange, klass, result)
        } else return null
    }
}