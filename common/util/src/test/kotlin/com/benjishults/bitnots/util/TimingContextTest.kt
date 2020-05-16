package com.benjishults.bitnots.util

import com.benjishults.bitnots.util.unsuspended.UnsuspendedTimedFunction
import com.benjishults.bitnots.util.unsuspended.UnsuspendedTimingScope
import com.benjishults.bitnots.util.unsuspended.unsuspendedTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

inline fun <T, R> suspend1(
    noinline block: suspend UnsuspendedTimingScope<T, R>.(T) -> R
): suspend UnsuspendedTimingScope<T, R>.(T) -> R =
    block


@ExperimentalTime
class TimingContextTest {

    val preSuspendTime = 5000L

    val pauseAfterContextExits = 50L

    @Test
    fun `test if timer starts immediately when context is active`() {
        val runner: suspend UnsuspendedTimingScope<String, String>.(String) -> String =
            suspend1<String, String> { _: String ->
                Thread.sleep(preSuspendTime)
                suspend { delay(preSuspendTime) }
                ""
            }
        runBlocking {

            val (result, millis) = unsuspendedTimeMillis<String, String>("arg", { _: String ->
                Thread.sleep(preSuspendTime)
                suspend { delay(preSuspendTime) }
                ""
            })
            //         Thread.sleep(pauseAfterContextExits)
            val timed = UnsuspendedTimedFunction<String, String> { t -> "" }
            Assertions.assertTrue(millis >= preSuspendTime, "took $millis which is less than $preSuspendTime")
            Assertions.assertTrue(
                millis < preSuspendTime + (pauseAfterContextExits / 2.0),
                "took $millis which is more than ${preSuspendTime + (pauseAfterContextExits / 2.0)}"
            )
        }
    }

}
