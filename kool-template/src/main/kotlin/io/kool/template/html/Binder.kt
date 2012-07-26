package io.kool.template.html

import java.util.ArrayList
import kotlin.dom.*
import org.w3c.dom.Node

/**
 * Used to bind expressions to nodes so that the binding can be re-evaluated later on when
 * the domain models have been updated.
 *
 * This approach is not as good as using an actual listener on the underlying domain model itself
 * but can be simpler to implement - and can be more efficient on low power devices.
 */
public class Binder {
    private val blocks = ArrayList<() -> Unit>()

    fun invoke(block: () -> Unit): Unit {
        blocks.add(block)
        block()
    }

    fun refresh(): Unit {
        for (block in blocks) {
            if (block != null) {
                block()
            }
        }
    }
}
