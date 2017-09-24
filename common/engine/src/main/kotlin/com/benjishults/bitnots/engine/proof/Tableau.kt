package com.benjishults.bitnots.engine.proof

import com.benjishults.bitnots.engine.proof.strategy.BooleanClosedIndicator
import com.benjishults.bitnots.engine.proof.strategy.ClosedIndicator
import com.benjishults.bitnots.engine.proof.strategy.ClosingStrategy
import com.benjishults.bitnots.engine.proof.strategy.FolStepStrategy
import com.benjishults.bitnots.engine.proof.strategy.FolUnificationClosingStrategy
import com.benjishults.bitnots.engine.proof.strategy.InitializingStrategy
import com.benjishults.bitnots.engine.proof.strategy.PropositionalClosingStrategy
import com.benjishults.bitnots.engine.proof.strategy.PropositionalInitializationStrategy
import com.benjishults.bitnots.engine.proof.strategy.PropositionalStepStrategy
import com.benjishults.bitnots.engine.proof.strategy.StepStrategy
import com.benjishults.bitnots.engine.unifier.MultiBranchCloser

interface Tableau<C : ClosedIndicator> {

    val root: TableauNode<C>
    fun isClosed() = root.isClosed()
    /**
     * Returns true if the step made a change to the receiver.
     */
    fun step(): Boolean
}

abstract class AbstractTableau<C : ClosedIndicator>(
        override val root: TableauNode<C>,
        val stepStrategy: StepStrategy<Tableau<C>>
) : Tableau<C> {

    override fun step(): Boolean =
            stepStrategy.step(this)
}

class PropositionalTableau(
        root: PropositionalTableauNode,
        stepStrategy: StepStrategy<Tableau<BooleanClosedIndicator>> = PropositionalStepStrategy<PropositionalTableauNode> { sf, p: PropositionalTableauNode ->
            PropositionalTableauNode(sf, p, PropositionalClosingStrategy(), PropositionalInitializationStrategy())
        }
) : AbstractTableau<BooleanClosedIndicator>(root, stepStrategy) {

    override fun toString(): String {
        return buildString {
            root.preOrderWithPath<PropositionalTableauNode> { n, path ->
                this.append(path.joinToString("."))
                this.append("\n")
                this.append(n.toString())
                this.append(n.initialClosers.toString())
                this.append("\n")
                this.append("\n")
                false
            }
        }
    }

}

class FolTableau(
        root: FolTableauNode,
        stepStrategy: StepStrategy<Tableau<*>> = FolStepStrategy { sf, p: FolTableauNode ->
            FolTableauNode(sf, p, FolUnificationClosingStrategy(), PropositionalInitializationStrategy())
        }
) : AbstractTableau<MultiBranchCloser>(root,  stepStrategy) {

    private fun createInitialSubstitutions() {
//        root.dep { node ->
//        true
//        }

    }

}
