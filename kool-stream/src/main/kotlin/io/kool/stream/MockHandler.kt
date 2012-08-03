package io.kool.stream

import io.kool.stream.*
import io.kool.stream.support.AbstractHandler
import io.kool.stream.support.Expectation
import io.kool.stream.support.assertSatisfied
import io.kool.stream.support.expectation
import io.kool.stream.support.isSatisfied
import java.util.ArrayList
import java.util.List
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
* A handler used for testing that enough messages are received.
*/
public class MockHandler<T>: AbstractHandler<T>() {
    private val _events: List<T> = ArrayList<T>()
    val expectations = ArrayList<Expectation>()
    val latch = CountDownLatch(1)

    public val events: List<T>
        get() {
            return synchronized(_events) {
                ArrayList<T>(_events)
            }
        }

    public override fun onNext(next: T) {
        synchronized(_events) {
            events.add(next)
        }
        fireEvents()
    }

    public fun expect(expectation: Expectation) {
        expectations.add(expectation)
    }

    public fun expect(failMessage: String, predicate: MockHandler<T>.() -> Boolean) {
        expect( expectation(failMessage) {
            this.predicate()
        })
    }

    /**
     * Adds an expectation that this handler receives the given number of elements
     */
    public fun expectReceive(count: Int) {
        expect("Has not received $count events") { events.size >= count }
    }

    /**
     * Returns true if the expectations are all met
     */
    public val expectationsSatisfied: Boolean = expectations.isSatisfied()

    /**
     * Asserts that all expectations are matched withing the given time period, closing the underlying stream whether assertions are valid or not
     * if the *closeStream* parameter is true
     */
    public fun assertExpectations(timeout: Long = 30000, units: TimeUnit = TimeUnit.MILLISECONDS, closeStream: Boolean = false) {
        try {
            if (!expectationsSatisfied) {
                latch.await(timeout, units)

                expectations.assertSatisfied()
            }
        } finally {
            if (closeStream) {
                close()
            }
        }
    }

    protected fun fireEvents() {
        if (expectationsSatisfied) {
            latch.countDown()
        }
    }
}
