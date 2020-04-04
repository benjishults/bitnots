package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.prover.Prover
import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.closer.TableauProofProgressIndicator
import com.benjishults.bitnots.tableau.strategy.TableauClosingStrategy
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

interface TableauProver<T : Tableau<*>> : Prover<T> {

    val finishingStrategy: TableauClosingStrategy<T>
    val stepStrategy: StepStrategy<T>
    val harness: Harness<T, *>

    /**
     * Expand the tableau a bit.
     * @return false if no step was possible
     */
    fun step(proofInProgress: T): Boolean = stepStrategy.step(proofInProgress)

    /**
     * Check whether the tableau can be closed.
     * @return an object that indicates how much, if any, work remains to be done.
     */
    fun searchForFinisher(proofInProgress: T): TableauProofProgressIndicator {
        return finishingStrategy.searchForClosure(proofInProgress)
    }

    override fun isDone(proofInProgress: T) = proofInProgress.indicator.isDone()

    override suspend fun prove(
        proofInProgress: T
    ) {
        while (coroutineContext.isActive) {
            val indicator = searchForFinisher(proofInProgress)
            proofInProgress.indicator = indicator
            if (indicator.isDone()) {
                return
            } else if (!coroutineContext.isActive) {
                break
            } else if (harness.rein(proofInProgress)) {
                return
            } else if (!step(proofInProgress)) {
                return
            }
        }
        // yield()
        error("Impossible!")
    }

}
