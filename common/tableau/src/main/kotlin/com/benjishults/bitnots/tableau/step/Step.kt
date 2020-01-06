package com.benjishults.bitnots.tableau.step

import com.benjishults.bitnots.prover.finish.ProofInProgress

interface Step<P : ProofInProgress> {
    fun apply(pip: P): Boolean
}
