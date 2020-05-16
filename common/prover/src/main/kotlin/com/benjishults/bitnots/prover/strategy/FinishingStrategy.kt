package com.benjishults.bitnots.prover.strategy

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.util.identity.Identified
import com.benjishults.bitnots.util.identity.Versioned

/**
 *
 * @param T type of proof in progress
 * @param I type of proof progress indicator
 */
interface FinishingStrategy<in P : ProofInProgress, out I : ProofProgressIndicator>: Versioned, Identified {
    /**
     * Check whether the proof is finished.
     * @return an object that indicates how much, if any, work remains to be done.
     */
    fun checkProgress(proofInProgress: P): I
}
