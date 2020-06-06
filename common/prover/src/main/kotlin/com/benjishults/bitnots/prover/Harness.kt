package com.benjishults.bitnots.prover

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.toConjunct
import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.util.identity.Identified
import com.benjishults.bitnots.util.identity.Versioned

interface Harness<T : ProofInProgress, out P : Prover<T>> : Versioned, Identified { // : CoroutineScope {

    // override val coroutineContext: CoroutineContext get() = Dispatchers.Default + SupervisorJob()
    val prover: P

    fun initializeProof(formula: Formula): T

    /**
     * @return true if this harness wants to prevent another step in the proof.
     */
    fun rein(proofInProgress: T): Boolean = false

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

    // suspend fun proveEachTarget(
    //     hyps: List<Formula>,
    //     targets: List<Formula>
    // ): Channel<T> = Channel<T>(10).also { channel ->
    //             targets.forEach { target ->
    //                     channel.send(proveWithHyps(hyps, target))
    //         }
    //     }

    suspend fun proveAllTargets(
        hyps: List<Formula>,
        targets: List<Formula>
    ): List<T> {
        val value = mutableListOf<T>()
        targets.forEach { target ->
            value.add(proveWithHyps(hyps, target))
        }
        return value
    }

    suspend fun prove(
        formula: Formula
    ) =
        initializeProof(formula).also { prove(it) }

    suspend fun prove(
        proofInProgress: T
    ): T {
        while (true) {
            proofInProgress.indicator = prover.checkProgress(proofInProgress)
            if (proofInProgress.indicator.isDone()) {
                return proofInProgress
            } else if (rein(proofInProgress)) {
                return proofInProgress
            } else if (!prover.step(proofInProgress)) {
                return proofInProgress
            }
        }
    }
}
