package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.strategy.PropositionalClosingStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalStepStrategy
import com.benjishults.bitnots.tableau.strategy.TableauClosingStrategy

class PropositionalTableauHarness(
    override val version: String = "unversioned"
) : Harness<PropositionalTableau> {

    override val finishingStrategy: TableauClosingStrategy<PropositionalTableau> = PropositionalClosingStrategy()
    override val stepStrategy: StepStrategy<PropositionalTableau> = PropositionalStepStrategy()

    override fun initializeProof(formula: Formula): PropositionalTableau =
        PropositionalTableau(formula)

}
