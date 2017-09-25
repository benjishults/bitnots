package com.benjishults.bitnots.engine.proof

import com.benjishults.bitnots.engine.proof.strategy.BooleanClosedIndicator
import com.benjishults.bitnots.engine.proof.strategy.InProgressTableauClosedIndicator
import com.benjishults.bitnots.engine.proof.strategy.NodeClosingStrategy
import com.benjishults.bitnots.engine.proof.strategy.NotCompatible
import com.benjishults.bitnots.engine.proof.strategy.PropositionalInitializationStrategy
import com.benjishults.bitnots.engine.proof.strategy.PropositionalNodeClosingStrategy
import com.benjishults.bitnots.engine.proof.strategy.PropositionalStepStrategy
import com.benjishults.bitnots.engine.proof.strategy.StepStrategy
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import java.util.Stack

interface Tableau {

    val root: TableauNode
    fun findCloser(): InProgressTableauClosedIndicator
    fun step(): Boolean
    /**
     * gets the latest found closer
     */
    fun getCloser(): InProgressTableauClosedIndicator

}

abstract class AbstractTableau(
        override val root: AbstractTableauNode,
        val nodeClosingStrategy: NodeClosingStrategy,
        val closedIndicatorFactory: (TableauNode) -> InProgressTableauClosedIndicator,
        val stepStrategy: StepStrategy<AbstractTableau>
) : Tableau {

    private var closer: InProgressTableauClosedIndicator = NotCompatible

    override fun getCloser(): InProgressTableauClosedIndicator {
        return closer
    }

    protected fun setCloser(closer: InProgressTableauClosedIndicator) {
        this.closer = closer
    }

    override fun findCloser(): InProgressTableauClosedIndicator { // = root.isClosed()
        nodeClosingStrategy.populateBranchClosers(this)
        val nodeSequence: (TableauNode) -> Sequence<TableauNode> = { n ->
            generateSequence {
                val stack = Stack<TableauNode>().apply { add(n) }
                stack.pop().also { it.children.reversed().forEach { stack.push(it as AbstractTableauNode) } }
            }
        }
        val toBeExtended = Stack<InProgressTableauClosedIndicator>().apply { push(closedIndicatorFactory(root)) }
        do {
            val inProgress = toBeExtended.pop()
            nodeSequence(inProgress.needToClose.peek()).forEach { node ->
                node.branchClosers.forEach { bc ->
                    inProgress.createExtension(bc).let { ext ->
                        ext.takeIf {
                            it.isCloser()
                        }?.let {
                            return it
                        } ?: toBeExtended.push(ext)
                    }
                }
            }
        } while (toBeExtended.isNotEmpty());
        return NotCompatible
    }

    /**
     * Returns true if the step made a change to the receiver.
     */
    override fun step(): Boolean =
            stepStrategy.step(this)
}

class PropositionalTableau(
        root: PropositionalTableauNode,
        stepStrategy: StepStrategy<Tableau> = PropositionalStepStrategy<PropositionalTableauNode> { sf, p: PropositionalTableauNode ->
            PropositionalTableauNode(sf, p, PropositionalInitializationStrategy())
        }
) : AbstractTableau(root, PropositionalNodeClosingStrategy, { BooleanClosedIndicator(it) }, stepStrategy) {
    //
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

//class FolTableau(
//        root: FolTableauNode,
//        stepStrategy: StepStrategy<Tableau> = FolStepStrategy { sf, p: FolTableauNode ->
//            FolTableauNode(sf, p, FolUnificationClosingStrategy(), PropositionalInitializationStrategy())
//        }
//) : AbstractTableau(root, stepStrategy) {
//
//    private fun createInitialSubstitutions() {
////        root.dep { node ->
////        true
////        }
//
//    }
//
//}
