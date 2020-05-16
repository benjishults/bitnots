package com.benjishults.bitnots.util.unsuspended

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.AbstractCoroutineContextKey
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * This marks the time this coroutine (and its children) is not suspended.
 */
@ExperimentalTime
class TimingContext : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {

    val millisUnsuspended: Long
        get() = duration.toLongMilliseconds()

    private var duration: Duration = Duration.ZERO
    private lateinit var mark: TimeMark

    init {
        TODO("see TimingScope")
    }

    @ExperimentalStdlibApi
    companion object Key : AbstractCoroutineContextKey<ContinuationInterceptor, TimingContext>(
        ContinuationInterceptor,
        { it as? TimingContext }
    )

    fun start() {
        mark = TimeSource.Monotonic.markNow()
    }

    fun end() {
        duration += mark.elapsedNow()
    }

    /**
     * Returns [continuation] but starts timing.
     */
    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
        start()
        return continuation
    }

    override fun releaseInterceptedContinuation(continuation: Continuation<*>) {
        end()
    }

}
