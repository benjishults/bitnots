package com.benjishults.bitnots.util

interface StepCounter {
    val steps: Long
    fun incrementSteps()

    companion object : () -> StepCounter {
        override fun invoke(): StepCounter = object : StepCounter {
            private var mySteps = 0L

            override val steps: Long
                get() = mySteps

            override fun incrementSteps() {
                mySteps++
            }
        }
    }

}
