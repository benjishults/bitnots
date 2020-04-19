package com.benjishults.bitnots.prover

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.TimeOutProofIndicator
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

interface TimedHarness<T : ProofInProgress, P : Prover<T>> : Harness<T, P> {

    val limitMillis: Long

    override suspend fun prove(
        proofInProgress: T
    ): T {
        if (limitMillis >= 0)
            try {
                withTimeout(limitMillis) {
                    super.prove(proofInProgress)
                }
            } catch (e: TimeoutCancellationException) {
                proofInProgress.indicator = TimeOutProofIndicator(limitMillis)
            }
        else
            super.prove(proofInProgress)
        return proofInProgress
    }

}

// suspend fun <V> withPossibleTimeout(limitMillis: Long, block: suspend CoroutineScope.() -> V, onTimeout: () -> V) : V=
//     if (limitMillis >= 0)
//         try {
//             withTimeout(limitMillis) {
//                 block()
//             }
//         } catch (e: TimeoutCancellationException) {
//             onTimeout()
//         }
//     else
//         block()
