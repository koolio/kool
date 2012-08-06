package io.kool.stream.support

import java.util.ArrayList
import kotlin.test.assertTrue

public trait Expectation {
    val failMessage: String

    /**
     * Returns true if the expectation is satisfied
     */
    public fun invoke(): Boolean
}

public fun expectation(failMessage: String, predicate: () -> Boolean): Expectation {
    return FunctionExpectation(failMessage, predicate)
}

/**
 * Represents an expectation which can be evaluated to be true or asserted that it is true
 */
private class FunctionExpectation(override val failMessage: String, val predicate: () -> Boolean): Expectation {

    /**
     * Returns true if the expectation is satisfied
     */
    public override fun invoke(): Boolean {
        return (predicate)()
    }

    public fun toString(): String = "FunctionExpectation(failMessage: $failMessage, predicate: $predicate)"
}

/**
 * Returns true if the expectations are satisfied (i.e. they are true)
 */
public fun java.lang.Iterable<Expectation>.isSatisfied(): Boolean {
    for (expectation in this) {
        if (expectation.invoke() == false)
            return false
    }
    return true
}

/**
 * Asserts that all the expectations are satisfied, throwing a failure exception for any that are not
 */
public fun java.lang.Iterable<Expectation>.assertSatisfied() {
    val failures = ArrayList<String>()
    for (expectation in this) {
        if (expectation() == false) {
            failures.add(expectation.failMessage)
        }
    }
    assertTrue(failures.isEmpty(), failures.makeString(", "))
}