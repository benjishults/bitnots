@file:Suppress("DEPRECATION_ERROR")

package com.benjishults.bitnots.util.unsuspended

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ChildHandle
import kotlinx.coroutines.ChildJob
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.selects.SelectClause0
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * This marks the time this coroutine (and its children) is not suspended
 */
@InternalCoroutinesApi
@ExperimentalTime
class TimingScope<T>(
    val uCont: Continuation<T>,
    parentContext: CoroutineContext,
    active: Boolean = true
) : Job {

    val millisUnsuspended: Long
        get() = duration.toLongMilliseconds()

    private var duration: Duration = Duration.ZERO
    private lateinit var mark: TimeMark

    init {
        TODO("looks like coroutines aren't ready for this kind of work, yet")
        if (active) startTimer()
    }

    private fun startTimer() {
        mark = TimeSource.Monotonic.markNow()
    }

    private fun stopTimer() {
        duration += mark.elapsedNow()
    }

    // override fun afterCompletion(state: Any?) {
    //     // Resume in a cancellable way by default when resuming from another context
    //     uCont.intercepted().resumeCancellableWith(recoverResult(state, uCont))
    // }
    //
    // override fun afterResume(state: Any?) {
    //     // Resume direct because scope is already in the correct context
    //     uCont.resumeWith(recoverResult(state, uCont))
    // }

    // internal fun initParentJobInternal(parent: Job?) {
    //     if (parent == null) {
    //         return
    //     }
    //     parent.start() // make sure the parent is started
    //     @Suppress("DEPRECATION")
    //     val handle = parent.attachChild(this)
    //     // now check our state _after_ registering (see tryFinalizeSimpleState order of actions)
    //     if (isCompleted) {
    //         handle.dispose()
    //     }
    // }
    // internal fun initParentJob() {
    //     initParentJobInternal(parentContext[Job])
    // }

    override val children: Sequence<Job>
        get() = TODO("Not yet implemented")
    override val isActive: Boolean
        get() = TODO("Not yet implemented")
    override val isCancelled: Boolean
        get() = TODO("Not yet implemented")
    override val isCompleted: Boolean
        get() = TODO("Not yet implemented")
    override val key: CoroutineContext.Key<*>
        get() = TODO("Not yet implemented")
    override val onJoin: SelectClause0
        get() = TODO("Not yet implemented")

    @InternalCoroutinesApi
    override fun attachChild(child: ChildJob): ChildHandle {
        TODO("Not yet implemented")
    }


    override fun cancel(cause: Throwable?): Boolean {
        TODO("Not yet implemented")
    }

    override fun cancel(cause: CancellationException?) {
        TODO("Not yet implemented")
    }

    @InternalCoroutinesApi
    override fun getCancellationException(): CancellationException {
        TODO("Not yet implemented")
    }

    @InternalCoroutinesApi
    override fun invokeOnCompletion(
        onCancelling: Boolean,
        invokeImmediately: Boolean,
        handler: CompletionHandler
    ): DisposableHandle {
        TODO("Not yet implemented")
    }

    override fun invokeOnCompletion(handler: CompletionHandler): DisposableHandle {
        TODO("Not yet implemented")
    }

    override suspend fun join() {
        TODO("Not yet implemented")
    }

    override fun start(): Boolean {
        TODO("Not yet implemented")
    }
}

// @ExperimentalTime
// @InternalCoroutinesApi
// fun <T, R> TimingScope<T>.startUndispatchedOrReturn(receiver: R, block: suspend R.() -> T): Any? {
//     initParentJob()
//     return undispatchedResult({ true }) {
//         block.startCoroutineUninterceptedOrReturn(receiver, this)
//     }
// }
//
// @InternalCoroutinesApi
// @ExperimentalTime
// suspend fun <R> timingScope(block: suspend CoroutineScope.() -> R): R =
//     suspendCoroutineUninterceptedOrReturn { uCont ->
//         val coroutine = TimingScope(uCont, uCont.context)
//         coroutine.startUndispatchedOrReturn(coroutine, block)
//     }
