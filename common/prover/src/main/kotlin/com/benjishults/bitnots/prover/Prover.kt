package com.benjishults.bitnots.prover

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.prover.strategy.FinishingStrategy
import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.util.identity.Identified
import com.benjishults.bitnots.util.identity.Versioned

interface Prover<in T : ProofInProgress>: Versioned, Identified {

    val finishingStrategy: FinishingStrategy<T, *>
    val stepStrategy: StepStrategy<T>

    /**
     * Take one step expanding the proof.
     * @return false if no step was possible
     */
    // TODO probably want to make this a suspend function so that I can check for cancellation
    //      more frequently than once per step
    fun step(proofInProgress: T): Boolean = stepStrategy.step(proofInProgress)

    /**
     * Check whether the proof is finished.
     * @return an object that indicates how much, if any, work remains to be done.
     */
    fun checkProgress(proofInProgress: T): ProofProgressIndicator {
        return finishingStrategy.checkProgress(proofInProgress)
    }

}
