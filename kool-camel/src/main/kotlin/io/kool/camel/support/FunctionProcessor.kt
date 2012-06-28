package io.kool.camel.support

import org.apache.camel.model.ExpressionNode
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.Exchange
import org.apache.camel.Processor

/**
 * Implements [[Processor]] using a function block
 */
public class FunctionProcessor(val block: Exchange.() -> Unit) : Processor {

    public override fun toString(): String? {
        return "FunctionProcessor($block)"
    }

    public override fun process(exchange: Exchange?) {
        if (exchange != null) {
            exchange.block()
        }
    }
}