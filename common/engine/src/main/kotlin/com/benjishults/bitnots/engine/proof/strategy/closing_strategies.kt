package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.AbstractTableauNode
import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.engine.unifier.MultiBranchCloser
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.inference.rules.DeltaFormula
import com.benjishults.bitnots.inference.rules.GammaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import java.util.Stack
import java.util.TreeSet

interface BranchCloser

interface NodeClosingStrategy {
    fun populateBranchClosers(tableau: Tableau)
}

object PropositionalNodeClosingStrategy : NodeClosingStrategy {
    override fun populateBranchClosers(tableau: Tableau) {
        tableau.root.preorderIterator<TableauNode>().let { iter ->
            while (iter.hasNext()) {
                iter.next().let { node ->
                    if (checkClosed(node)) {
                        iter.skipMode = true
                    } else {
                        iter.skipMode = false
                    }
                }
            }
        }
    }

    @Suppress("USELESS_CAST")
    fun checkClosed(node: TableauNode): Boolean =
            with(node) {
                // TODO might want to cache these or make them easier to access
                val pos: MutableList<PropositionalVariable> = mutableListOf()
                val neg: MutableList<PropositionalVariable> = mutableListOf()
                allFormulas.forEach {
                    if (it.formula is PropositionalVariable) {
                        if (it.sign)
                            pos.add(it.formula as PropositionalVariable)
                        else
                            neg.add(it.formula as PropositionalVariable)
                    } else if (it is ClosingFormula) {
                        return true
                    }
                }
                return pos.any { p -> neg.any { it === p } }
            }

}

interface InProgressTableauClosedIndicator {
    fun createExtension(closer: BranchCloser): InProgressTableauClosedIndicator
    val needToClose: Stack<out TableauNode>
    fun isCloser() = needToClose.isEmpty()
}

/**
 * It's appearance indicates that an attempt was made to extend an InProgressTableauClosedIndicator with a BranchCloser with which it was not compatible.
 */
object NotCompatible : InProgressTableauClosedIndicator {
    override fun createExtension(closer: BranchCloser) = this

    override val needToClose: Stack<TableauNode>
        get() = TODO()
}

open class BooleanClosedIndicator(node: TableauNode) : InProgressTableauClosedIndicator {
    override val needToClose = Stack<TableauNode>().apply { add(node) }

    // FIXME should be immutable
    // FIXME seems different arguments are needed here
    override fun createExtension(closer: BranchCloser): InProgressTableauClosedIndicator {
        if (closer in needToClose.peek().branchClosers) {
            needToClose.pop()
            return this
        } else {
            return NotCompatible
        }
    }
}

interface ClosingStrategy {
    fun checkClosed(node: TableauNode): InProgressTableauClosedIndicator
}

//open class FolUnificationClosingStrategy : ClosingStrategy {
//    override fun checkClosed(node: TableauNode): MultiBranchCloser {
//        with(node) {
//            // TODO might want to cache these or make them easier to access
//            val pos: MutableList<PropositionalVariable> = mutableListOf()
//            val neg: MutableList<PropositionalVariable> = mutableListOf()
//            allFormulas.forEach {
//                if (it.formula is PropositionalVariable) {
//                    if (it.sign)
//                        pos.add(it.formula as PropositionalVariable)
//                    else
//                        neg.add(it.formula as PropositionalVariable)
//                } else if (it is ClosingFormula) {
//                    return Closed
//                }
//            }
//            pos.any { p -> neg.any { it === p } }.takeIf { it }?.let { Closed } ?: NotClosed
//        }
//    }
//
//}

interface InitializingStrategy {
    fun init(node: TableauNode)
}

interface PropositionalInitializingStrategy : InitializingStrategy {
    override fun init(node: TableauNode) {
        with(node) {
            applyAllAlphas(node)
            applyRegularity(node)
            allFormulas.addAll(newFormulas)
        }
    }

    fun applyAllAlphas(node: TableauNode)
    fun applyRegularity(node: TableauNode)
}

open class PropositionalInitializationStrategy : PropositionalInitializingStrategy {

    override fun applyAllAlphas(node: TableauNode) {
        with(node) {
            while (true) {
                val toAdd: MutableList<SignedFormula<Formula<*>>> = mutableListOf()
                newFormulas.iterator().let {
                    while (it.hasNext()) {
                        val current = it.next()
                        if (current is AlphaFormula) {
                            it.remove()
                            toAdd.addAll(current.generateChildren());
                        }
                    }
                }
                if (toAdd.isEmpty())
                    break
                else
                    newFormulas.addAll(toAdd)
            }
        }
    }

    override fun applyRegularity(node: TableauNode) {
        with(node) {
            newFormulas.iterator().let { iter ->
                while (iter.hasNext()) {
                    iter.next().let {
                        if (it in allFormulas)
                            iter.remove()
                    }
                }
            }
        }
    }

}

interface StepStrategy<in T : Tableau> {
    fun step(tableau: T): Boolean
}

open class PropositionalStepStrategy<N : TableauNode>(
        val nodeFactory: (MutableList<SignedFormula<*>>, N) -> N
) : StepStrategy<Tableau> {
    override open fun step(tableau: Tableau): Boolean =
            with(tableau) {
                applyBeta(tableau)// { fs, le -> PropositionalTableauNode(fs, le, closingStrategy, initStrategy) }
            }

    fun applyBeta(tableau: Tableau): Boolean =
            with(tableau) {
                var beta: BetaFormula<Formula<*>>? = null
                val node = root.breadthFirst<TableauNode> {
                    beta = it.newFormulas.firstOrNull { it is BetaFormula<*> } as BetaFormula<*>?
                    beta !== null
                }
                if (node === null)
                    false
                else
                    beta?.let { beta ->
                        node.newFormulas.remove(beta);
                        val leaves = node.allLeaves<N>()
                        leaves.map { leaf ->
                            beta.generateChildren().map {
                                leaf.children.add(nodeFactory(mutableListOf(it), leaf))
                            }
                        }
                        true
                    } ?: false
            }

}

//open class FolStepStrategy(
//        nodeFactory: (MutableList<SignedFormula<*>>, FolTableauNode) -> FolTableauNode
//) : PropositionalStepStrategy<FolTableauNode>(nodeFactory) {
//    override open fun step(tableau: Tableau): Boolean =
//            with(tableau) {
//                if (!applyDelta(tableau) && !applyBeta(tableau)) {
//                    if (unify(tableau).needToClose.isEmpty())
//                        true
//                    else
//                        applyGamma(tableau)
//                } else {
//                    true
//                }
//            }
//
//    fun unify(tableau: Tableau): MultiBranchCloser {
//        with(tableau) {
//            root.breadthFirst<FolTableauNode> { node ->
//                node.toString()
//                true
//            }
//            return MultiBranchCloser()
//        }
//    }
//
//    // TODO make this splice
//    private fun applyDelta(tableau: Tableau): Boolean {
//        with(tableau) {
//            var delta: DeltaFormula<out VarBindingFormula>? = null
//            val node = root.breadthFirst<FolTableauNode> {
//                delta = it.newFormulas.firstOrNull { it is DeltaFormula<*> } as DeltaFormula<*>?
//                delta !== null
//            }
//            if (node === null)
//                return false
//            delta?.let {
//                delta ->
//                node.newFormulas.remove(delta);
//                val leaves = node.allLeaves<FolTableauNode>()
//                leaves.forEach<FolTableauNode> { leaf ->
//                    delta.generateChildren().forEach {
//                        leaf.children.add(nodeFactory(mutableListOf(it), leaf))
//                        Unit
//                    }
//                }
//                return true
//            } ?: return false
//        }
//    }
//
//    // TODO make this splice
//    private fun applyGamma(tableau: Tableau): Boolean {
//        with(tableau) {
//            var gammas: MutableSet<Pair<GammaFormula<*>, FolTableauNode>> =
//                    TreeSet<Pair<GammaFormula<*>, FolTableauNode>>(
//                            Comparator<Pair<GammaFormula<*>, FolTableauNode>> {
//                                o1: Pair<GammaFormula<*>, FolTableauNode>?, o2: Pair<GammaFormula<*>, FolTableauNode>? ->
//                                o1!!.first.numberOfApplications.compareTo(o2!!.first.numberOfApplications)
//                            })
//            root.breadthFirst<FolTableauNode> { node ->
//                gammas.addAll(node.newFormulas.filterIsInstance<GammaFormula<*>>().map {
//                    it to node
//                }) // stop if we get to one that is ready to go
//                        && gammas.first().first.numberOfApplications == 0
//            }
//            gammas.firstOrNull()?.let {
//                (gamma, node) ->
//                gamma.numberOfApplications++
//                val leaves = node.allLeaves<FolTableauNode>()
//                leaves.map { leaf ->
//                    gamma.generateChildren().map {
//                        leaf.children.add(nodeFactory(mutableListOf(it), leaf))
//                    }
//                }
//                return true
//            } ?: return false
//        }
//    }
//
//}
