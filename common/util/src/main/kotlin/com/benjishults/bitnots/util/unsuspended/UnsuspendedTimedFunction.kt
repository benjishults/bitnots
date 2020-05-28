package com.benjishults.bitnots.util.unsuspended

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

// class UnsuspendedTimedFunction<R>(
//     val block: suspend UnsuspendedTimingScope<R>.() -> R
// )

// /**
//  * Any suspension of the coroutine (or its children) will stop the timer and any resumption of the coroutine (or any of its children) will resume the timer.
//  *
//  * @return the pair (result, millis) where [result] is the of calling the block and [millis] is the number of millis threads spent doing the work.
//  */
// @ExperimentalTime
// suspend fun <R> unsuspendedTimeMillis(block: UnsuspendedTimingScope<R>.() -> R): Pair<R, Long> {
//     withContext(UnsuspendedTimingScope(coroutineContext)) {
//
//     }
// suspendCoroutine { continuation: Continuation<R> ->
//
//     block()
//     execQuery(query) { qr, conn ->
//         continuation.resume(qr to conn) //resume gives the value for suspendCoroutine to return
//     }
// }
//     return UnsuspendedTimingScopeImpl(block).runCallLoop()
// }

class UnsuspendedTimingScope : CoroutineContext {
    override fun <R> fold(initial: R, operation: (R, CoroutineContext.Element) -> R): R {
        TODO("Not yet implemented")
    }

    override fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>): E? {
        TODO("Not yet implemented")
    }

    override fun minusKey(key: CoroutineContext.Key<*>): CoroutineContext {
        TODO("Not yet implemented")
    }

}

// @RestrictsSuspension
// sealed class UnsuspendedTimingScope<R> {
//     @Deprecated(
//         level = DeprecationLevel.ERROR,
//         message =
//         "'invoke' should not be called from UnsuspendedTimingScope. " +
//                 "Use 'callRecursive' to do recursion in the heap instead of the call stack.",
//         replaceWith = ReplaceWith("this.callRecursive(value)")
//     )
//     operator fun UnsuspendedTimedFunction<*>.invoke(value: Any?): Nothing =
//         throw UnsupportedOperationException("Should not be called from UnsuspendedTimedFunction")
// }

// ================== Implementation ==================

private typealias UnsuspendedTimedFunctionBlock = (Any?, Continuation<Any?>) -> Any? // Function3<Any?, Continuation<Any?>?, Any?>

private val UNDEFINED_RESULT = Result.success(COROUTINE_SUSPENDED)

// @Suppress("UNCHECKED_CAST")
// @ExperimentalTime
// private class UnsuspendedTimingScopeImpl<R>(
//     block: UnsuspendedTimingScope<R>.() -> R
// ) : UnsuspendedTimingScope<R>(), Continuation<R> {
//     val millisUnsuspended: Long
//         get() = duration.toLongMilliseconds()
//
//     private var duration: Duration = Duration.ZERO
//     private lateinit var mark: TimeMark
//
//     // Active function block
//     private var function: UnsuspendedTimedFunctionBlock = block as UnsuspendedTimedFunctionBlock
//
//     // Continuation of the current call
//     private var cont: Continuation<Any?>? = this as Continuation<Any?>
//
//     // Completion result (completion of the whole call stack)
//     private var result: Any? = UNDEFINED_RESULT
//
//     override val context: CoroutineContext
//         get() = EmptyCoroutineContext
//
//     override fun resumeWith(result: Result<R>) {
//         this.cont = null
//         stopTimer()
//         this.result = result
//     }
//
//     private fun startTimer() {
//         mark = TimeSource.Monotonic.markNow()
//     }
//
//     fun resume(value: R) {
//         startTimer()
//         (this as Continuation<R>).resume(value)
//     }
//
//     private fun stopTimer() {
//         duration += mark.elapsedNow()
//     }
//
//     @Suppress("UNCHECKED_CAST")
//     fun runCallLoop(): Pair<R, Long> {
//         while (true) {
//             // Note: cont is set to null in UnsuspendedTimingScopeImpl.resumeWith when the whole computation completes
//             val result = this.result
//             val cont = this.cont
//                 ?: return result as R to millisUnsuspended // done -- final result
//             // The order of comparison is important here for that case of rogue class with broken equals
//             if (UNDEFINED_RESULT == result) {
//                 // call "function" with "value" using "cont" as completion
//                 val r = try {
//                     startTimer()
//                     // This is block.startCoroutine(this, value, cont)
//                     function(this, cont)
//                     stopTimer()
//                 } catch (e: Throwable) {
//                     stopTimer()
//                     cont.resumeWithException(e)
//                     continue
//                 }
//                 // If the function returns without suspension -- calls its continuation immediately
//                 if (r !== COROUTINE_SUSPENDED) {
//                     startTimer()
//                     cont.resume(r as R)
//                     stopTimer()
//                 }
//             } else {
//                 // we returned from a crossFunctionCompletion trampoline -- call resume here
//                 this.result = UNDEFINED_RESULT // reset result back
//                 cont.resumeWith(Result.success(result))
//             }
//         }
//     }
// }
