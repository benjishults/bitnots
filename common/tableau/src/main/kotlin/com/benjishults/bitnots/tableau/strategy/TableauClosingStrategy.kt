package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.prover.strategy.FinishingStrategy
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.closer.InProgressTableauClosedIndicator

interface TableauClosingStrategy: FinishingStrategy<Tableau, InProgressTableauClosedIndicator> {
    fun populateBranchClosers(tableau: Tableau)
}
