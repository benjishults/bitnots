package com.benjishults.bitnots.prover

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.prover.strategy.FinishingStrategy
import com.benjishults.bitnots.prover.strategy.StepStrategy

interface Prover<in T : ProofInProgress> {

    val version: String
    val finishingStrategy: FinishingStrategy<T, *>
    val stepStrategy: StepStrategy<T>

    /**
     * Take one step expanding the proof.
     * @return false if no step was possible
     */
    fun step(proofInProgress: T): Boolean = stepStrategy.step(proofInProgress)

    /**
     * Check whether the proof is finished.
     * @return an object that indicates how much, if any, work remains to be done.
     */
    fun checkProgress(proofInProgress: T): ProofProgressIndicator {
        return finishingStrategy.checkProgress(proofInProgress)
    }

}
