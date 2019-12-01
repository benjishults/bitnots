package com.benjishults.bitnots.engine.proof

import com.benjishults.bitnots.engine.proof.closer.UnifyingClosedIndicator
import com.benjishults.bitnots.engine.proof.strategy.FolStepStrategy
import com.benjishults.bitnots.engine.proof.strategy.FolUnificationClosingStrategy
import com.benjishults.bitnots.engine.proof.strategy.PropositionalInitializationStrategy
import com.benjishults.bitnots.engine.proof.strategy.StepStrategy

class FolTableau(
    root: FolTableauNode,
    stepStrategy: StepStrategy<Tableau> = FolStepStrategy { sf, p ->
        FolTableauNode(sf, p as FolTableauNode?, PropositionalInitializationStrategy())
    }
) : AbstractTableau(root, FolUnificationClosingStrategy(), { UnifyingClosedIndicator(it) }, stepStrategy) {

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
