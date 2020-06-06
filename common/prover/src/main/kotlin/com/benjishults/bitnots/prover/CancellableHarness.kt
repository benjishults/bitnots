package com.benjishults.bitnots.prover

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.prover.finish.ProofInProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

interface CancellableHarness<T : ProofInProgress, out P : Prover<T>> : Harness<T, P> { // : CoroutineScope {

    // TODO I want a cancellable channel with smart lifecycle handling.
    suspend fun proveEachTarget(
        hyps: List<Formula>,
        targets: List<Formula>
    ): Channel<T> = Channel<T>(10).also { channel ->
        withContext(Dispatchers.Default) {
            //     TODO how to specify the exception handler?
            supervisorScope {
                targets.forEach { target ->
                    coroutineScope {
                        channel.send(proveWithHyps(hyps, target))
                    }
                }
            }
        }
    }

    // TODO I want a cancellable channel with smart lifecycle handling.
    override suspend fun proveAllTargets(
        hyps: List<Formula>,
        targets: List<Formula>
    ): List<T> = runBlocking {
        val value = mutableListOf<T>()
        targets.forEach { target ->
            launch(Dispatchers.Default) {
                value.add(proveWithHyps(hyps, target))
            }
        }
        value
    }

    /**
     * This implementation stops proving when it notices that the coroutineContext has been canceled.
     */
    override suspend fun prove(
        proofInProgress: T
    ): T {
        while (coroutineContext.isActive) {
            proofInProgress.indicator = prover.checkProgress(proofInProgress)
            if (proofInProgress.indicator.isDone()) {
                return proofInProgress
            } else if (!coroutineContext.isActive) {
                return proofInProgress
            } else if (rein(proofInProgress)) {
                return proofInProgress
            } else if (!prover.step(proofInProgress)) {
                return proofInProgress
            }
        }
        error("Impossible")
    }

}
