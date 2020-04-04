package com.benjishults.bitnots.util

class StepCounterImpl(steps: Long = 0L) : StepCounter {
    private var steps = steps
    override fun getSteps() =
        steps

    override fun incrementSteps() {
        steps++
    }
}
