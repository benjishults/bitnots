package com.benjishults.bitnots.prover

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.TimeOutProofIndicator
import com.benjishults.bitnots.util.Timed
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

interface TimedHarness<T : ProofInProgress, P : Prover<T>> : CancellableHarness<T, P> {

    val limitMillis: Long

    override suspend fun prove(
        proofInProgress: T
    ): T {
        if (limitMillis >= 0 && proofInProgress is Timed)
            try {
                withTimeout(limitMillis) {
                    proofInProgress.addTime { super.prove(proofInProgress) }
                }
            } catch (e: TimeoutCancellationException) {
                proofInProgress.indicator = TimeOutProofIndicator(limitMillis)
            }
        else
            super.prove(proofInProgress)
        return proofInProgress
    }

}
