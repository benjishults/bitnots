package com.benjishults.bitnots.prover

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.prover.finish.ProofInProgress
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

interface Harness<T : ProofInProgress, out P : Prover<T>> {

    val prover: P

    fun initializeProof(formula: Formula): T

    /**
     * @return true if this harness wants to prevent another step in the proof.
     */
    fun rein(proofInProgress: T): Boolean = false

    suspend fun prove(
        formula: Formula
    ) =
        initializeProof(formula).also { prove(it) }

    suspend fun prove(
        proofInProgress: T
    ): T {
        while (coroutineContext.isActive) {
            val indicator = prover.checkProgress(proofInProgress)
            proofInProgress.indicator = indicator
            if (indicator.isDone()) {
                return proofInProgress
            } else if (!coroutineContext.isActive) {
                break
            } else if (rein(proofInProgress)) {
                return proofInProgress
            } else if (!prover.step(proofInProgress)) {
                return proofInProgress
            }
        }
        error("Impossible!")
    }

}
