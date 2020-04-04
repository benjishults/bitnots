package com.benjishults.bitnots.prover.finish

interface ProofProgressIndicator {
    fun isDone(): Boolean
    // fun <T: Any> extendBy(extension: T): ProofProgressIndicator
}

interface FailedProofIndicator : ProofProgressIndicator {
    override fun isDone(): Boolean = false
}

open class TimeOutProofIndicator(
    val allowedMillis: Long
) : FailedProofIndicator

interface EngineErrorIndicator<out T : Any> : FailedProofIndicator {
    val reason: T?
}

/**
 * We don't care or don't have interesting information about how it was done.
 */
interface SuccessfulProofIndicator : ProofProgressIndicator {
    override fun isDone(): Boolean = true
}
