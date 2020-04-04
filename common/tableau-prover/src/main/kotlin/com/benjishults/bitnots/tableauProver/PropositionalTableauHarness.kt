package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.strategy.PropositionalClosingStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalStepStrategy

object PropositionalTableauHarness :
    Harness<PropositionalTableau, PropositionalFormulaProver> {

    override fun toProver(): PropositionalFormulaProver {
        return PropositionalFormulaProver(
            PropositionalClosingStrategy(),
            PropositionalStepStrategy(),
            this
        )
    }

}
