package com.benjishults.bitnots.util

import kotlin.system.measureTimeMillis

interface Timed {
    val millis: Long
    fun addTime(addedMillis: Long)

    suspend fun <T> addTime(block: suspend () -> T): T? {
        var value: T? = null
        addTime(measureTimeMillis { value = block() })
        return value
    }

    /**
     * Provides a default impl
     */
    companion object : () -> Timed {
        override fun invoke(): Timed =
            object : Timed {
                private var myMillis = 0L
                override val millis: Long
                    get() = myMillis

                override fun addTime(addedMillis: Long) {
                    myMillis += addedMillis
                }
            }
    }

}
