package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.FolTableauNode
import com.benjishults.bitnots.tableau.closer.InProgressTableauClosedIndicator
import com.benjishults.bitnots.tableau.closer.ExtensionFailed
import com.benjishults.bitnots.tableau.closer.TableauProofProgressIndicator
import com.benjishults.bitnots.tableau.strategy.FolStepStrategy
import com.benjishults.bitnots.tableau.strategy.FolUnificationClosingStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalInitializationStrategy

class FolFormulaTableauProver(
        target: Formula<*>,
        override val finishingStrategy: FolUnificationClosingStrategy,
        override val stepStrategy: FolStepStrategy
) : TableauProver<FolTableau, InProgressTableauClosedIndicator> {
    override var indicator: TableauProofProgressIndicator = ExtensionFailed
    override val proofInProgress =
            FolTableau(
                    PropositionalInitializationStrategy.init(
                            FolTableauNode(
                                    mutableListOf(target.createSignedFormula()))))

}
