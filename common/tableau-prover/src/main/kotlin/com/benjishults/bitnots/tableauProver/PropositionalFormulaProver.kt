package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.util.isPropositional
import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.PropositionalTableauNode
import com.benjishults.bitnots.tableau.closer.ExtensionFailed
import com.benjishults.bitnots.tableau.closer.InProgressTableauClosedIndicator
import com.benjishults.bitnots.tableau.closer.TableauProofProgressIndicator
import com.benjishults.bitnots.tableau.strategy.PropositionalClosingStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalInitializationStrategy

open class PropositionalFormulaProver(
        target: Formula<*>,
        override val finishingStrategy: PropositionalClosingStrategy,
        override val stepStrategy: StepStrategy<PropositionalTableau>
) : TableauProver<PropositionalTableau, InProgressTableauClosedIndicator> {

    final override val proofInProgress: PropositionalTableau =
            PropositionalTableau(
                    PropositionalInitializationStrategy.init(
                            PropositionalTableauNode(
                                    mutableListOf(target.createSignedFormula()))))

    init {
        require(target.isPropositional())
    }

    override var indicator: TableauProofProgressIndicator = ExtensionFailed

}
