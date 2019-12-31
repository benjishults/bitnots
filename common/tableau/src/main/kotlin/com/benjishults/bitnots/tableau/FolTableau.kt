package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.closer.UnifyingClosedIndicator
import com.benjishults.bitnots.tableau.strategy.FolStepStrategy
import com.benjishults.bitnots.tableau.strategy.FolUnificationClosingStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalInitializationStrategy

class FolTableau(
        root: FolTableauNode,
        stepStrategy: StepStrategy<Tableau> = FolStepStrategy { sf, p ->
            FolTableauNode(sf, p as FolTableauNode?, PropositionalInitializationStrategy())
        }
) : AbstractTableau(
        root,
        FolUnificationClosingStrategy(),
        { UnifyingClosedIndicator(it) },
        stepStrategy) {

    override fun toString(): String {
        return buildString {
            root.preOrderWithPath<FolTableauNode> { n, path ->
                this.append(path.joinToString("."))
                this.append("\n")
                this.append(n.toString())
                //                this.append(n.initialClosers.toString())
                this.append("\n")
                this.append("\n")
                false
            }
        }
    }

}
