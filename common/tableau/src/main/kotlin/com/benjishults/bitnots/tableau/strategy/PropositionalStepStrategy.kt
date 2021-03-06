package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.PropositionalTableauNode
import com.benjishults.bitnots.tableau.step.BetaStep
import com.benjishults.bitnots.util.identity.CommitIdTimeVersioner
import com.benjishults.bitnots.util.identity.Identified
import com.benjishults.bitnots.util.identity.Versioned

open class PropositionalStepStrategy(
) : StepStrategy<PropositionalTableau>, Versioned by CommitIdTimeVersioner, Identified by Identified {

    private val betaStep: BetaStep<PropositionalTableau, PropositionalTableauNode> =
        BetaStep { formula, tableauNode ->
            PropositionalInitializationStrategy.init(PropositionalTableauNode(mutableListOf(formula), tableauNode))
        }

    override fun step(proofInProgress: PropositionalTableau): Boolean {
        proofInProgress.incrementCount()
        return betaStep.apply(proofInProgress)
    }

}
