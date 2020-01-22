package com.benjishults.bitnots.prover.finish

interface ProofProgressIndicator {
    fun isDone(): Boolean
}

interface FailedProofIndicator : ProofProgressIndicator {
    override fun isDone(): Boolean = false
}

interface TimeOutProofIndicator : FailedProofIndicator {
    val allowedMillis: Long
}

interface EngineErrorIndicator<out T : Any> : FailedProofIndicator {
    val reason: T?
}

/**
 * We don't care or don't have interesting information about how it was done.
 */
interface SuccessfulProofIndicator : ProofProgressIndicator {
    override fun isDone(): Boolean = true
}
