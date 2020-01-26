package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.PropositionalTableauNode
import com.benjishults.bitnots.tableau.step.BetaStep

open class PropositionalStepStrategy(
) : StepStrategy<PropositionalTableau> {

    private val betaStep: BetaStep<PropositionalTableau, PropositionalTableauNode> =
            BetaStep { formula, tableauNode ->
                PropositionalInitializationStrategy.init(PropositionalTableauNode(mutableListOf(formula), tableauNode))
            }

    override fun step(proofInProgress: PropositionalTableau): Boolean = betaStep.apply(proofInProgress)

}
