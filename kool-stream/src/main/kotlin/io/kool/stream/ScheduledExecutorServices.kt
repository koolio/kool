package io.kool.stream

import io.kool.stream.support.*
import java.util.concurrent.*

/**
* Creates a [[TimerTask]] for the given handler
*/
fun Handler<Long>.toTimerRunnable(): Runnable {
    val handler = this
    return runnable {
        val time = System.currentTimeMillis()
        handler.onNext(time)
    }
}


/**
 * Creates a [[Stream]] of a single time event to occur at the given *delay*
 */
fun ScheduledExecutorService.scheduleStream(delay: Long, unit: TimeUnit = TimeUnit.MILLISECONDS!!) = ScheduledFutureStream {
    schedule(it, delay, unit)
}.take(1)

/**
 * Creates a [[Stream]] of timer events to occur at a *fixed rate execution* with given *period* and optional *initialDelay*
 */
fun ScheduledExecutorService.scheduleAtFixedRateStream(period: Long, initialDelay: Long = 0, unit: TimeUnit = TimeUnit.MILLISECONDS!!) = ScheduledFutureStream {
    scheduleAtFixedRate(it, initialDelay, period, unit)
}

/**
 * Creates a [[Stream]] of timer events to occur at a *fixed delay execution* with given *period* and optional *initialDelay*
 */
fun ScheduledExecutorService.scheduleWithFixedDelayStream(period: Long, initialDelay: Long = 0, unit: TimeUnit = TimeUnit.MILLISECONDS!!) = ScheduledFutureStream {
    scheduleWithFixedDelay(it, initialDelay, period, unit)
}
