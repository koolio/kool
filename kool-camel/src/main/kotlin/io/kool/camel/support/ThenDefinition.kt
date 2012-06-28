package io.kool.camel.support

import org.apache.camel.model.ExpressionNode
import org.apache.camel.model.ProcessorDefinition

/**
 * Supports the *then* syntax in the DSL to chain predicates and action blocks
 */
public class ThenDefinition<T: ProcessorDefinition<T>>(val expressionBlock: ExpressionNode) {

    public fun then(block: ExpressionNode.() -> Any): T {
        expressionBlock.block()
        return expressionBlock.getParent()!! as T
    }
}