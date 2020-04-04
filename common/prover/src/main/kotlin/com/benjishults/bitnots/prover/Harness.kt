package com.benjishults.bitnots.prover

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.TimeOutProofIndicator
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.coroutineContext

interface Harness<T : ProofInProgress> : Prover<T> {

    fun initializeProof(formula: Formula): T

    /**
     * @return true if this harness wants to prevent another step in the proof.
     */
    fun rein(proofInProgress: T): Boolean = false

    suspend fun limitedTimeProve(
        proofInProgress: T,
        millis: Long
    ): ProofInProgress {
        if (millis >= 0)
            try {
                withTimeout(millis) {
                    prove(proofInProgress)
                }
            } catch (e: TimeoutCancellationException) {
                proofInProgress.indicator = TimeOutProofIndicator(millis)
            }
        else
            prove(proofInProgress)
        return proofInProgress
    }

    suspend fun limitedTimeProve(
        formula: Formula,
        millis: Long
    ): ProofInProgress {
        val proofInProgress = initializeProof(formula)
        if (millis >= 0)
            try {
                withTimeout(millis) {
                    prove(proofInProgress)
                }
            } catch (e: TimeoutCancellationException) {
                proofInProgress.indicator = TimeOutProofIndicator(millis)
            }
        else
            prove(proofInProgress)
        return proofInProgress
    }

    suspend fun prove(
        formula: Formula,
        millis: Long
    ): ProofInProgress {
        val proofInProgress = initializeProof(formula)
        while (coroutineContext.isActive) {
            val indicator = checkProgress(proofInProgress)
            proofInProgress.indicator = indicator
            if (indicator.isDone()) {
                return proofInProgress
            } else if (!coroutineContext.isActive) {
                break
            } else if (rein(proofInProgress)) {
                return proofInProgress
            } else if (!step(proofInProgress)) {
                return proofInProgress
            }
        }
        // yield()
        error("Impossible!")
    }

    suspend fun prove(
        proofInProgress: T
    ) {
        while (coroutineContext.isActive) {
            val indicator = checkProgress(proofInProgress)
            proofInProgress.indicator = indicator
            if (indicator.isDone()) {
                return
            } else if (!coroutineContext.isActive) {
                break
            } else if (rein(proofInProgress)) {
                return
            } else if (!step(proofInProgress)) {
                return
            }
        }
        // yield()
        error("Impossible!")
    }

}
