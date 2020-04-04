package com.benjishults.bitnots.prover

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.TimeOutProofIndicator
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

interface Prover<in T : ProofInProgress> {

    val version: String

    /**
     * Long-running and as exhaustive as allowed.
     */
    suspend fun prove(proofInProgress: T)

    /**
     * Should be fast as a lookup.
     */
    fun isDone(proofInProgress: T): Boolean

    fun limitedTimeProve(
        proofInProgress: T,
        millis: Long
    ): ProofInProgress {
        if (millis >= 0)
            try {
                runBlocking {
                    withTimeout(millis) {
                        prove(proofInProgress)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                proofInProgress.indicator = TimeOutProofIndicator(millis)
            }
        else runBlocking {
            prove(proofInProgress)
        }
        return proofInProgress
    }

}
