package io.kool.stream

import java.util.*
import kotlin.concurrent.*
import io.kool.stream.support.*

/**
* Creates a [[TimerTask]] for the given handler
*/
fun Handler<Long>.toTimerTask(): TimerTask {
    val handler = this
    return timerTask {
        val time = System.currentTimeMillis()
        handler.onNext(time)
    }
}


/**
 * Creates a [[Stream]] of time events with the *fixed delay execution* of the given millisecond *period* between events starting after the *delay* in milliseconds
 */
fun Timer.fixedDelayStream(period: Long, delay: Long = 0): Stream<Long> = TimerStream {
    schedule(it, delay, period)
}

/**
 * Creates a [[Stream]] of time events with the *fixed delay execution* if the given millisecond *period* between events starting at the given *firstTime*
 */
fun Timer.fixedDelayStream(period: Long, firstTime: Date): Stream<Long> = TimerStream {
    schedule(it, firstTime, period)
}

/**
 * Creates a [[Stream]] of time events at a *fixed rate execution* with the given millisecond *period* between events starting after the *delay* in milliseconds
 */
fun Timer.fixedRateStream(period: Long, delay: Long = 0): Stream<Long> = TimerStream {
    scheduleAtFixedRate(it, delay, period)
}

/**
 * Creates a [[Stream]] of time events at a *fixed rate execution* with the given millisecond *period* between events starting at the given *firstTime*
 */
fun Timer.fixedRateStream(period: Long, firstTime: Date): Stream<Long> = TimerStream {
    scheduleAtFixedRate(it, firstTime, period)
}

/**
 * Creates a [[Stream]] of a single time event scheduled to occur in the given *delay* milliseconds
 */
fun Timer.scheduleStream(delay: Long): Stream<Long> = TimerStream {
    schedule(it, delay)
}.take(1)

/**
 * Creates a [[Stream]] of a single time event to occur at the given *time*
 */
fun Timer.scheduleStream(time: Date): Stream<Long> = TimerStream {
    schedule(it, time)
}.take(1)
