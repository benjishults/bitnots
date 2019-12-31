package com.benjishults.bitnots.prover.strategy

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator

/**
 * @param T type of proof in progress
 * @param I type of proof progress indicator
 */
interface FinishingStrategy<in T: ProofInProgress, out I: ProofProgressIndicator> {
    fun tryFinish(proofInProgress: T) : I
}

