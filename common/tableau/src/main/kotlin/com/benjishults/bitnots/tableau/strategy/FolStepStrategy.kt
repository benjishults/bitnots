package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.FolTableauNode
import com.benjishults.bitnots.tableau.step.BetaStep
import com.benjishults.bitnots.tableau.step.DeltaStep
import com.benjishults.bitnots.tableau.step.GammaStep
import com.benjishults.bitnots.util.identity.CommitIdTimeVersioner
import com.benjishults.bitnots.util.identity.Identified
import com.benjishults.bitnots.util.identity.Versioned

open class FolStepStrategy(
    val qLimit: Long = 3
) : StepStrategy<FolTableau>, Versioned by CommitIdTimeVersioner, Identified by Identified {

    private val nodeFactory = { formula: SignedFormula<*>, tableauNode: FolTableauNode ->
        PropositionalInitializationStrategy.init(
            FolTableauNode(mutableListOf(formula), tableauNode)
        )
    }

    private val betaStep = BetaStep<FolTableau, FolTableauNode>(nodeFactory)
    private val deltaStep = DeltaStep<FolTableau, FolTableauNode>(nodeFactory)
    private val gammaStep = GammaStep<FolTableau, FolTableauNode>(qLimit, nodeFactory)

    override fun step(proofInProgress: FolTableau): Boolean {
        proofInProgress.incrementCount()
        var taken = false
        while (deltaStep.apply(proofInProgress)) {
            taken = true
        }
        // TODO if gamma applied, no sense looking for closure unless it produces a SimpleSignedFormula
        return taken || betaStep.apply(proofInProgress) || gammaStep.apply(proofInProgress)
    }

}
