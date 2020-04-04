package com.benjishults.bitnots.prover

import com.benjishults.bitnots.prover.finish.ProofInProgress

interface Harness<T : ProofInProgress, out P: Prover<T>> {
    fun toProver() : P
    fun rein(proofInProgress: T) : Boolean = false
}
