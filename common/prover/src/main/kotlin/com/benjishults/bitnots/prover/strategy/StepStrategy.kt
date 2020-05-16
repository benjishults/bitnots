package com.benjishults.bitnots.prover.strategy

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.util.identity.Identified
import com.benjishults.bitnots.util.identity.Versioned

interface StepStrategy<in T : ProofInProgress>: Versioned, Identified {
    fun step(proofInProgress: T): Boolean
}
