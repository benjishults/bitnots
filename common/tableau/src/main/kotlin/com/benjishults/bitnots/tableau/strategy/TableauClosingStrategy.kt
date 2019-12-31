package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.tableau.ProofInProgress
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.closer.InProgressTableauClosedIndicator
import com.benjishults.bitnots.tableau.closer.ProofProgressIndicator

/**
 * @param T type of proof in progress
 * @param I type of proof progress indicator
 */
interface FinishingStrategy<in T: ProofInProgress, out I: ProofProgressIndicator> {
    fun tryFinish(proofInProgress: T) : I
}

interface TableauClosingStrategy: FinishingStrategy<Tableau, InProgressTableauClosedIndicator> {
    fun populateBranchClosers(tableau: Tableau)
}
