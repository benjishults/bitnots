package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.strategy.FolStepStrategy
import com.benjishults.bitnots.tableau.strategy.FolUnificationClosingStrategy

data class FolTableauHarness(
    val qLimit: Long = 3L,
    val stepLimit: Long = -1L,
    val timeLimitMillis: Long = -1L,
    val version: String = "unversioned"
) : Harness<FolTableau, FolFormulaTableauProver> {

    override fun toProver(): FolFormulaTableauProver {
        return FolFormulaTableauProver(
            FolUnificationClosingStrategy(),
            FolStepStrategy(qLimit),
            this,
            version
        )
    }

    override fun rein(proofInProgress: FolTableau): Boolean {
        return stepLimit >= 0 && proofInProgress.getSteps() == stepLimit
    }

}
