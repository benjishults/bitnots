package com.benjishults.bitnots.prover.strategy

import com.benjishults.bitnots.prover.finish.ProofInProgress

interface StepStrategy<in T : ProofInProgress> {
    fun step(proofInProgress: T): Boolean
}
