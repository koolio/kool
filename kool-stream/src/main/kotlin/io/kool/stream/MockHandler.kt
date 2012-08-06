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
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertTrue

/**
* A handler used for testing that enough messages are received.
*/
public class MockHandler<T>: AbstractHandler<T>() {
    private val _events: List<T> = ArrayList<T>()
    val expectations = ArrayList<Expectation>()
    val openLatch = CountDownLatch(1)
    val assertLatch = CountDownLatch(1)
    val closeLatch = CountDownLatch(1)
    val opened = AtomicBoolean(false)

    public val events: List<T>
        get() {
            return synchronized(_events) {
                ArrayList<T>(_events)
            }
        }

    public override fun onOpen(cursor: Cursor) {
        super.onOpen(cursor)
        openLatch.countDown()
        opened.set(true)
    }


    public override fun close() {
        super.close()
        closeLatch.countDown()
    }

    public override fun onNext(next: T) {
        synchronized(_events) {
            _events.add(next)
        }
        fireEvents()
    }

    public fun expect(expectation: Expectation): MockHandler<T> {
        expectations.add(expectation)
        return this
    }

    public fun expect(failMessage: String, predicate: MockHandler<T>.() -> Boolean): MockHandler<T> {
        return expect( expectation(failMessage) {
            this.predicate()
        })
    }

    /**
     * Adds an expectation that this handler receives the given number of elements
     */
    public fun expectReceive(count: Int): MockHandler<T> {
        return expect("Has not received $count events") {
            println("Current events are: $events")
            events.size >= count
        }
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
                assertLatch.await(timeout, units)

                expectations.assertSatisfied()
            }
        } finally {
            if (closeStream) {
                close()
            }
        }
    }

    public fun assertWaitForOpen(timeout: Long = 30000, units: TimeUnit = TimeUnit.MILLISECONDS) {
        if (!opened.get()) {
            openLatch.await(timeout, units)
        }
        assertTrue(opened.get(), "$this has not been opened yet")
    }

    public fun assertWaitForClose(timeout: Long = 30000, units: TimeUnit = TimeUnit.MILLISECONDS) {
        if (!isClosed()) {
            closeLatch.await(timeout, units)
        }
        assertTrue(isClosed(), "$this has not been closed yet")
    }

    protected fun fireEvents() {
        if (expectationsSatisfied) {
            assertLatch.countDown()
        }
    }
}
