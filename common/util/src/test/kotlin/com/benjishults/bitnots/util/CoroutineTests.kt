package com.benjishults.bitnots.util

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.CoroutineContext

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

    suspend fun test2() {
        val job = kotlin.coroutines.coroutineContext[Job]
        coroutineScope {

        }

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
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

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

