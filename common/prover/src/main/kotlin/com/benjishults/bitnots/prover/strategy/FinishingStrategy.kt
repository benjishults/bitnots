package com.benjishults.bitnots.prover.strategy

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator

/**
 *
 * @param T type of proof in progress
 * @param I type of proof progress indicator
 */
interface FinishingStrategy<in P : ProofInProgress, out I : ProofProgressIndicator> {
    /**
     * Check whether the proof is finished.
     * @return an object that indicates how much, if any, work remains to be done.
     */
    fun checkProgress(proofInProgress: P): I
}
