package com.benjishults.bitnots.tableauProver

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

    /**
     * Expand the tableau a bit.
     */
    fun step(proofInProgress: T): Boolean = stepStrategy.step(proofInProgress)

    /**
     * Check whether the tableau can be closed.
     * @return an object that indicates how much, if any, work remains to be done.
     */
    fun searchForFinisher(proofInProgress: T): TableauProofProgressIndicator {
        return finishingStrategy.searchForClosure(proofInProgress)
    }

    override fun isDone(proofInProgress: T) = proofInProgress.closer().isDone()

    override suspend fun prove(
            proofInProgress: T
    ): TableauProofProgressIndicator {
        while (coroutineContext.isActive) {
            val indicator = searchForFinisher(proofInProgress)
            if (indicator.isDone()) {
                return indicator
            } else if (!coroutineContext.isActive) {
                break
            } else if (!step(proofInProgress)) {
                return indicator
            }
        }
        // yield()
        error("Impossible!")
    }

}
