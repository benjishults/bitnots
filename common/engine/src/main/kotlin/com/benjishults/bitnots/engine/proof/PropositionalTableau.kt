package com.benjishults.bitnots.engine.proof

import com.benjishults.bitnots.engine.proof.closer.BooleanClosedIndicator
import com.benjishults.bitnots.engine.proof.strategy.PropositionalClosingStrategy
import com.benjishults.bitnots.engine.proof.strategy.PropositionalInitializationStrategy
import com.benjishults.bitnots.engine.proof.strategy.PropositionalStepStrategy
import com.benjishults.bitnots.engine.proof.strategy.StepStrategy

class PropositionalTableau(
        root: PropositionalTableauNode,
        stepStrategy: StepStrategy<Tableau> = PropositionalStepStrategy<PropositionalTableauNode> { sf, p: PropositionalTableauNode ->
            PropositionalTableauNode(sf, p, PropositionalInitializationStrategy())
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
