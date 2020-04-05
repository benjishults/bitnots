package com.benjishults.bitnots.prover

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.TimeOutProofIndicator
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

interface TimedHarness<T : ProofInProgress, P : Prover<T>> : Harness<T, P> {

    val limitMillis: Long

    suspend fun limitedTimeProve(
        proofInProgress: T
    ): ProofInProgress {
        if (limitMillis >= 0)
            try {
                withTimeout(limitMillis) {
                    prove(proofInProgress)
                }
            } catch (e: TimeoutCancellationException) {
                proofInProgress.indicator = TimeOutProofIndicator(limitMillis)
            }
        else
            prove(proofInProgress)
        return proofInProgress
    }

    suspend fun limitedTimeProve(
        formula: Formula
    ): ProofInProgress =
        initializeProof(formula).also { limitedTimeProve(it) }

}
