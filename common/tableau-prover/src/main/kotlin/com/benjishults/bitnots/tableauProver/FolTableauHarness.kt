package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.strategy.FolStepStrategy
import com.benjishults.bitnots.tableau.strategy.FolUnificationClosingStrategy

data class FolTableauHarness(val qLimit: Long?, val stepLimit: Long?, val timeLimitMillis: Long?) :
        Harness<FolTableau, FolFormulaTableauProver> {

    init {
        require(qLimit != null ||
                stepLimit != null ||
                timeLimitMillis != null)
    }

    override fun toProver(): FolFormulaTableauProver {
        return FolFormulaTableauProver(FolUnificationClosingStrategy(),
                                       qLimit?.let {
                                           FolStepStrategy(it)
                                       }
                                       ?: FolStepStrategy())
    }

}
