package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.AbstractTableauNode
import com.benjishults.bitnots.engine.proof.FolTableauNode
import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.engine.unifier.MultiBranchCloser
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.inference.rules.DeltaFormula
import com.benjishults.bitnots.inference.rules.GammaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import java.util.TreeSet

interface ClosedIndicator {
    fun isCloser(): Boolean
}

sealed class BooleanClosedIndicator(val closer: Boolean) : ClosedIndicator {
    override fun isCloser() = closer
}

object Closed : BooleanClosedIndicator(true)
object NotClosed : BooleanClosedIndicator(false)


interface ClosingStrategy<in N : AbstractTableauNode<C>, C : ClosedIndicator> {
    fun checkClosed(node: N): C
}

open class PropositionalClosingStrategy : ClosingStrategy<AbstractTableauNode<BooleanClosedIndicator>, BooleanClosedIndicator> {

    @Suppress("USELESS_CAST")
    override fun checkClosed(node: AbstractTableauNode<BooleanClosedIndicator>): BooleanClosedIndicator =
            with(node) {
                // TODO might want to cache these or make them easier to access
                val pos: MutableList<PropositionalVariable> = mutableListOf()
                val neg: MutableList<PropositionalVariable> = mutableListOf()
                allFormulas.map {
                    if (it.formula is PropositionalVariable) {
                        if (it.sign)
                            pos.add(it.formula as PropositionalVariable)
                        else
                            neg.add(it.formula as PropositionalVariable)
                    }
                }
                pos.any { p -> neg.any { it === p } }.takeIf { it }?.let { Closed } ?: NotClosed
            }
}

open class FolUnificationClosingStrategy : ClosingStrategy<AbstractTableauNode<MultiBranchCloser>, MultiBranchCloser> {
    override fun checkClosed(node: AbstractTableauNode<MultiBranchCloser>): MultiBranchCloser {
        TODO()
    }

}

interface InitializingStrategy<in T : TableauNode<*>> {
    fun init(node: T)
}

interface PropositionalInitializingStrategy<in T : AbstractTableauNode<*>> : InitializingStrategy<T> {
    override fun init(node: T) {
        with(node) {
            applyAllAlphas(node)
            applyRegularity(node)
            allFormulas.addAll(newFormulas)
        }
    }

    fun applyAllAlphas(node: T)
    fun applyRegularity(node: T)
}

open class PropositionalInitializationStrategy : PropositionalInitializingStrategy<AbstractTableauNode<*>> {

    override fun applyAllAlphas(node: AbstractTableauNode<*>) {
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

    override fun applyRegularity(node: AbstractTableauNode<*>) {
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

interface StepStrategy<in T : Tableau<*>> {
    fun step(tableau: T): Boolean
}

open class PropositionalStepStrategy<N : AbstractTableauNode<*>>(
        val nodeFactory: (MutableList<SignedFormula<*>>, N) -> N
) : StepStrategy<Tableau<*>> {
    override open fun step(tableau: Tableau<*>): Boolean =
            with(tableau) {
                applyBeta(tableau)// { fs, le -> PropositionalTableauNode(fs, le, closingStrategy, initStrategy) }
            }

    fun applyBeta(tableau: Tableau<*>): Boolean =
            with(tableau) {
                var beta: BetaFormula<Formula<*>>? = null
                val node = root.breadthFirst<TableauNode<*>> {
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

open class FolStepStrategy(
        nodeFactory: (MutableList<SignedFormula<*>>, FolTableauNode) -> FolTableauNode
) : PropositionalStepStrategy<FolTableauNode>(nodeFactory) {
    override open fun step(tableau: Tableau<*>): Boolean =
            with(tableau) {
                if (!applyDelta(tableau) && !applyBeta(tableau)) {
                    if (unify(tableau).needToClose.isEmpty())
                        true
                    else
                        applyGamma(tableau)
                } else {
                    true
                }
            }

    fun unify(tableau: Tableau<*>): MultiBranchCloser {
        with(tableau) {
            root.breadthFirst<FolTableauNode> { node ->
                node.toString()
                true
            }
            return MultiBranchCloser()
        }
    }

    // TODO make this splice
    private fun applyDelta(tableau: Tableau<*>): Boolean {
        with(tableau) {
            var delta: DeltaFormula<out VarBindingFormula>? = null
            val node = root.breadthFirst<FolTableauNode> {
                delta = it.newFormulas.firstOrNull { it is DeltaFormula<*> } as DeltaFormula<*>?
                delta !== null
            }
            if (node === null)
                return false
            delta?.let {
                delta ->
                node.newFormulas.remove(delta);
                val leaves = node.allLeaves<FolTableauNode>()
                leaves.forEach<FolTableauNode> { leaf ->
                    delta.generateChildren().forEach {
                        leaf.children.add(nodeFactory(mutableListOf(it), leaf))
                        Unit
                    }
                }
                return true
            } ?: return false
        }
    }

    // TODO make this splice
    private fun applyGamma(tableau: Tableau<*>): Boolean {
        with(tableau) {
            var gammas: MutableSet<Pair<GammaFormula<*>, FolTableauNode>> =
                    TreeSet<Pair<GammaFormula<*>, FolTableauNode>>(
                            Comparator<Pair<GammaFormula<*>, FolTableauNode>> {
                                o1: Pair<GammaFormula<*>, FolTableauNode>?, o2: Pair<GammaFormula<*>, FolTableauNode>? ->
                                o1!!.first.numberOfApplications.compareTo(o2!!.first.numberOfApplications)
                            })
            root.breadthFirst<FolTableauNode> { node ->
                gammas.addAll(node.newFormulas.filterIsInstance<GammaFormula<*>>().map {
                    it to node
                }) // stop if we get to one that is ready to go
                        && gammas.first().first.numberOfApplications == 0
            }
            gammas.firstOrNull()?.let {
                (gamma, node) ->
                gamma.numberOfApplications++
                val leaves = node.allLeaves<FolTableauNode>()
                leaves.map { leaf ->
                    gamma.generateChildren().map {
                        leaf.children.add(nodeFactory(mutableListOf(it), leaf))
                    }
                }
                return true
            } ?: return false
        }
    }

}
