package com.benjishults.bitnots.util

import kotlinx.coroutines.coroutineScope
import org.junit.jupiter.api.Test
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.ExperimentalTime

@ExperimentalTime
class TimingContextTest {

    val preSuspendTime = 5000L

    val pauseAfterContextExits = 50L

    @Test
    fun test4() {
        val block: () -> String = {
            "test"
        }
        suspend {
            suspendCoroutine<Pair<Result<String>, Long>> {continuation ->
                continuation.resume(Result.success(block()) to 0L)
            }
            coroutineScope {

            }
            coroutineContext
        }
    }

    // @Test
    // fun `test if timer starts immediately when context is active`() {
    //     val runner: suspend UnsuspendedTimingScope<String, String>.(String) -> String =
    //         suspend1<String, String> { _: String ->
    //             Thread.sleep(preSuspendTime)
    //             suspend { delay(preSuspendTime) }
    //             ""
    //         }
    //     runBlocking {
    //
    //         val (result, millis) = unsuspendedTimeMillis<String, String>("arg", { _: String ->
    //             Thread.sleep(preSuspendTime)
    //             suspend { delay(preSuspendTime) }
    //             ""
    //         })
    //         //         Thread.sleep(pauseAfterContextExits)
    //         val timed = UnsuspendedTimedFunction<String, String> { t -> "" }
    //         Assertions.assertTrue(millis >= preSuspendTime, "took $millis which is less than $preSuspendTime")
    //         Assertions.assertTrue(
    //             millis < preSuspendTime + (pauseAfterContextExits / 2.0),
    //             "took $millis which is more than ${preSuspendTime + (pauseAfterContextExits / 2.0)}"
    //         )
    //     }
    // }

}
