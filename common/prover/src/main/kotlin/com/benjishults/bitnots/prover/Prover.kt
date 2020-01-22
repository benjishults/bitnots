package com.benjishults.bitnots.prover

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator

interface Prover<in T : ProofInProgress> {

    /**
     * Long-running and as exhaustive as allowed by the receiver type.
     */
    suspend fun prove(proofInProgress: T): ProofProgressIndicator

    /**
     * Should be fast as a lookup.
     */
    fun isDone(proofInProgress: T): Boolean

}
