package com.benjishults.bitnots.util.unsuspended

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource


class UnsuspendedTimedFunction<T, R>(
    internal val block: suspend UnsuspendedTimingScope<T, R>.(T) -> R
)

@ExperimentalTime
fun <T, R> unsuspendedTimeMillis(arg: T, block: suspend UnsuspendedTimingScope<T, R>.(T) -> R): Pair<Result<R>, Long> {
    return UnsuspendedTimingScopeImpl(block, arg).runCallLoop()
}

@RestrictsSuspension
sealed class UnsuspendedTimingScope<T, R> {
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message =
        "'invoke' should not be called from UnsuspendedTimingScope. " +
                "Use 'callRecursive' to do recursion in the heap instead of the call stack.",
        replaceWith = ReplaceWith("this.callRecursive(value)")
    )
    operator fun UnsuspendedTimedFunction<*, *>.invoke(value: Any?): Nothing =
        throw UnsupportedOperationException("Should not be called from UnsuspendedTimedFunction")
}

// ================== Implementation ==================

private typealias UnsuspendedTimedFunctionBlock = (Any?, Any?, Continuation<Any?>) -> Any?// Function3<Any?, Any?, Continuation<Any?>?, Any?>

private val UNDEFINED_RESULT = Result.success(COROUTINE_SUSPENDED)

@Suppress("UNCHECKED_CAST")
@ExperimentalTime
private class UnsuspendedTimingScopeImpl<T, R>(
    block: suspend UnsuspendedTimingScope<T, R>.(T) -> R,
    value: T
) : UnsuspendedTimingScope<T, R>(), Continuation<R> {
    val millisUnsuspended: Long
        get() = duration.toLongMilliseconds()

    private var duration: Duration = Duration.ZERO
    private lateinit var mark: TimeMark

    // init {
    //     startTimer()
    // }

    // Active function block
    private var function: UnsuspendedTimedFunctionBlock = block as UnsuspendedTimedFunctionBlock

    // Value to call function with
    private var value: Any? = value

    // Continuation of the current call
    private var cont: Continuation<Any?>? = this as Continuation<Any?>

    // Completion result (completion of the whole call stack)
    private var result: Result<Any?> = UNDEFINED_RESULT

    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    override fun resumeWith(result: Result<R>) {
        this.cont = null
        stopTimer()
        this.result = result
    }

    private fun startTimer() {
        mark = TimeSource.Monotonic.markNow()
    }

    fun resume(value: R) {
        startTimer()
        (this as Continuation<R>).resume(value)
    }

    private fun stopTimer() {
        duration += mark.elapsedNow()
    }

    @Suppress("UNCHECKED_CAST")
    fun runCallLoop(): Pair<Result<R>, Long> {
        while (true) {
            // Note: cont is set to null in UnsuspendedTimingScopeImpl.resumeWith when the whole computation completes
            val result = this.result
            val cont = this.cont
                ?: return result as Result<R> to millisUnsuspended // done -- final result
            // The order of comparison is important here for that case of rogue class with broken equals
            if (UNDEFINED_RESULT == result) {
                // call "function" with "value" using "cont" as completion
                val r = try {
                    startTimer()
                    // This is block.startCoroutine(this, value, cont)
                    function(this, value, cont)
                    stopTimer()
                } catch (e: Throwable) {
                    stopTimer()
                    cont.resumeWithException(e)
                    continue
                }
                // If the function returns without suspension -- calls its continuation immediately
                if (r !== COROUTINE_SUSPENDED) {
                    startTimer()
                    cont.resume(r as R)
                    stopTimer()
                }
            } else {
                // we returned from a crossFunctionCompletion trampoline -- call resume here
                this.result = UNDEFINED_RESULT // reset result back
                cont.resumeWith(result)
            }
        }
    }
}
