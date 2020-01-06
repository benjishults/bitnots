package com.benjishults.bitnots.prover

import com.benjishults.bitnots.prover.finish.ProofInProgress

interface ProofMonitor<out P : ProofInProgress> {

    val proofInProgress: P
    /**
     * Should be fast as a lookup.
     */
    fun isDone(): Boolean

}
