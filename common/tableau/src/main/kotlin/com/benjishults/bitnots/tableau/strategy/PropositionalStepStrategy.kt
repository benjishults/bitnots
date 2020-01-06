package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.PropositionalTableauNode
import com.benjishults.bitnots.tableau.step.BetaStep

open class PropositionalStepStrategy(
        nodeFactory: (SignedFormula<*>, PropositionalTableauNode) -> PropositionalTableauNode
) : StepStrategy<PropositionalTableau> {

    private val betaStep: BetaStep<PropositionalTableau, PropositionalTableauNode> = BetaStep(nodeFactory)

    override fun step(proofInProgress: PropositionalTableau): Boolean = betaStep.apply(proofInProgress)

}
