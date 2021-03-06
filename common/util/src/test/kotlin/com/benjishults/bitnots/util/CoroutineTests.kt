package com.benjishults.bitnots.util

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError
import java.time.Duration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// import kotlin.coroutines.coroutineContext

val smallRange = 1..10
val range = 1..1_000
val largeRange = 1..1_000_000
val hugeRange = 1..1_000_000_000_000_000_000

class TestNonCoroutineScope {

    // fun test0() {
    //     // cannot call suspend function here
    //     kotlin.coroutines.coroutineContext
    // }

    // fun test1() {
    //     // cannot call suspend function here
    //     coroutineScope {
    //     }
    // }

    fun test4() {
        val block: () -> String = {
            "test"
        }
        suspend {
            suspendCoroutine<Pair<Result<String>, Long>> { continuation ->
                continuation.resume(Result.success(block()) to 0L)
            }
            coroutineScope {

            }
            coroutineContext
        }
    }

    @Test
    fun `the loop takes less than 800 ms`() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(800L)) {
            runBlocking {
                repeat(5) {
                    launch(Dispatchers.Default) {
                        Thread.sleep(500L)
                    }
                }
            }
        }
    }

    @Test
    fun `the loop takes at least 500 ms`() {
        Assertions.assertThrows(AssertionFailedError::class.java) {
            Assertions.assertTimeoutPreemptively(Duration.ofMillis(500L)) {
                runBlocking {
                    repeat(5) {
                        launch(Dispatchers.Default) {
                            Thread.sleep(500L)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `the loop takes at least 1500 ms`() {
        Assertions.assertThrows(AssertionFailedError::class.java) {
            Assertions.assertTimeoutPreemptively(Duration.ofMillis(1500L)) {
                runBlocking {
                    repeat(5) {
                        launch {
                            Thread.sleep(500L)
                        }
                    }
                }
            }
        }
    }

    // fun test5() {
    //     suspend {
    //         coroutineScope {
    //
    //         }
    //         coroutineContext
    //     }()
    // }

    suspend fun test2() {
        val job = coroutineContext[Job]
        coroutineScope {

        }
        suspend {
            coroutineScope {

            }
            coroutineContext
        }()


        supervisorScope {

        }
    }

    suspend fun test3() {
        // val job = Job()
        val job = kotlin.coroutines.coroutineContext[Job]

        val supervisor = SupervisorJob()

    }

}

class TestCoroutineScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default

    fun test0() {
        val job = coroutineContext[Job]
        //     // cannot call suspend function here
        // kotlin.coroutines.coroutineContext
    }

    // fun test1() {
    //     // cannot call suspend function here
    //     coroutineScope {
    //     }
    // }

    suspend fun test2() {
        val job = coroutineContext[Job]
        val job2 = kotlin.coroutines.coroutineContext[Job]
        coroutineScope {

        }
        supervisorScope {

        }

    }

}

class TestCoroutineScopeWithJob : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job + CoroutineExceptionHandler { coroutineContext, throwable -> }

    fun test0() {
        val job = coroutineContext[Job]
    }

    // fun test1() {
    //     // cannot call suspend function here
    //     coroutineScope {
    //     }
    // }

    suspend fun test2() {
        val job = coroutineContext[Job]
        val job2 = kotlin.coroutines.coroutineContext[Job]
        coroutineScope {

        }
        supervisorScope {

        }

    }

}

class TestCoroutineScopeWithSupervisorJob : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job + CoroutineExceptionHandler { coroutineContext, throwable -> }

    fun test0() {
        val CoroutineExceptionHandler = coroutineContext[CoroutineExceptionHandler]
    }

    // fun test1() {
    //     // cannot call suspend function here
    //     coroutineScope {
    //     }
    // }

    suspend fun test2() {
        val job = coroutineContext[Job]
        val job2 = kotlin.coroutines.coroutineContext[Job]
        coroutineScope {

        }
        supervisorScope {

        }

    }

}

