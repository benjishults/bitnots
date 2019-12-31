package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.closer.BooleanClosedIndicator
import com.benjishults.bitnots.tableau.strategy.PropositionalClosingStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalInitializationStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalStepStrategy

class PropositionalTableau(
        root: PropositionalTableauNode,
        stepStrategy: StepStrategy<Tableau> = PropositionalStepStrategy { sf, p ->
            PropositionalTableauNode(sf, p as PropositionalTableauNode?, PropositionalInitializationStrategy())
        }
) : AbstractTableau(root, PropositionalClosingStrategy, { BooleanClosedIndicator(it) }, stepStrategy) {

    override fun toString(): String {
        return buildString {
            root.preOrderWithPath<PropositionalTableauNode> { n, path ->
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
