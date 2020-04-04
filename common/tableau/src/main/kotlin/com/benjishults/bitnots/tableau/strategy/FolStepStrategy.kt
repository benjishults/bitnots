package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.FolTableauNode
import com.benjishults.bitnots.tableau.step.BetaStep
import com.benjishults.bitnots.tableau.step.DeltaStep
import com.benjishults.bitnots.tableau.step.GammaStep

open class FolStepStrategy(
    val qLimit: Long = 3
) : StepStrategy<FolTableau> {

    private val nodeFactory = { formula: SignedFormula<*>, tableauNode: FolTableauNode ->
        PropositionalInitializationStrategy.init(
            FolTableauNode(mutableListOf(formula), tableauNode)
        )
    }

    private val betaStep = BetaStep<FolTableau, FolTableauNode>(nodeFactory)
    private val deltaStep = DeltaStep<FolTableau, FolTableauNode>(nodeFactory)
    private val gammaStep = GammaStep<FolTableau, FolTableauNode>(qLimit, nodeFactory)

    override fun step(proofInProgress: FolTableau): Boolean {
        proofInProgress.incrementSteps()
        var taken = false
        while (deltaStep.apply(proofInProgress)) {
            taken = true
        }
        return if (!taken) {
            // TODO if gamma applied, no sense looking for closure unless it produces a SimpleSignedFormula
            betaStep.apply(proofInProgress) || gammaStep.apply(proofInProgress)
        } else {
            true
        }
    }

}
