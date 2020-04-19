package com.benjishults.bitnots.prover

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.util.toConjunct
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

    suspend fun proveWithHyps(
        hyps: List<Formula>,
        target: Formula
    ): T =
        prove(
            hyps.toConjunct()?.let {
                Implies(
                    it,
                    target
                )
            } ?: target)

    suspend fun proveAllTargets(
        hyps: List<Formula>,
        targets: List<Formula>
    ): Collection<T> =
        mutableListOf<T>().also { value ->
            targets.forEach { target ->
                value.add(proveWithHyps(hyps, target))
            }
        }

    suspend fun prove(
        formula: Formula
    ) =
        initializeProof(formula).also { prove(it) }

    suspend fun prove(
        proofInProgress: T
    ): T {//=  withContext(Dispatchers.Default) {
        while (coroutineContext.isActive) {
            proofInProgress.indicator = prover.checkProgress(proofInProgress)
            if (proofInProgress.indicator.isDone()) {
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
