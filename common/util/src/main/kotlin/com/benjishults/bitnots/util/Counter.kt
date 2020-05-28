package com.benjishults.bitnots.util

interface Counter {
    val count: Long
    fun incrementCount()

    companion object : () -> Counter {
        override fun invoke(): Counter = object : Counter {
            private var myCount = 0L

            override val count: Long
                get() = myCount

            override fun incrementCount() {
                myCount++
            }
        }
    }

}
