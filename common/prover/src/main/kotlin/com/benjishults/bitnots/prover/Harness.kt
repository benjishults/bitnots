package com.benjishults.bitnots.prover

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.util.toConjunct
import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.util.identity.Identified
import com.benjishults.bitnots.util.identity.Versioned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

interface Harness<T : ProofInProgress, out P : Prover<T>>: Versioned, Identified { // : CoroutineScope {

    // override val coroutineContext: CoroutineContext get() = Dispatchers.Default + SupervisorJob()
    val prover: P

    fun initializeProof(formula: Formula): T

    /**
     * @return true if this harness wants to prevent another step in the proof.
     */
    suspend fun rein(proofInProgress: T): Boolean = false

    suspend fun proveWithHyps(
        hyps: List<Formula>,
        target: Formula
    ): T =
        prove(
            formula = hyps.toConjunct()?.let {
                Implies(
                    it,
                    target
                )
            } ?: target)

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
    suspend fun proveAllTargets(
        hyps: List<Formula>,
        targets: List<Formula>
    ): List<T> =
        withContext(Dispatchers.Default) {
            val value = mutableListOf<T>()
            targets.forEach { target ->
                value.add(proveWithHyps(hyps, target))
            }
            value
        }

    suspend fun prove(
        formula: Formula
    ) =
        initializeProof(formula).also { prove(it) }

    suspend fun prove(
        proofInProgress: T
    ): T = withContext(Dispatchers.Default) {
        // measureTimeMillis {  }
        while (coroutineContext.isActive) {
            proofInProgress.indicator = prover.checkProgress(proofInProgress)
            if (proofInProgress.indicator.isDone()) {
                return@withContext proofInProgress
            } else if (!coroutineContext.isActive) {
                break
            } else if (rein(proofInProgress)) {
                return@withContext proofInProgress
            } else if (!prover.step(proofInProgress)) {
                return@withContext proofInProgress
            }
        }
        error("Impossible!")
    }

}
