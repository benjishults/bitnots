package com.benjishults.bitnots.engine.proof

import com.benjishults.bitnots.engine.unifier.MultiBranchCloser
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.inference.rules.DeltaFormula
import com.benjishults.bitnots.inference.rules.GammaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula

interface Tableau<out T : TableauNode> {
    val root: T
    fun isClosed() = root.isClosed()
    fun step(): Boolean


}

open class PropositionalTableau<T : PropositionalTableauNode>(
        override val root: T
) : Tableau<T> {
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

    override fun step(): Boolean {
        return applyBeta<PropositionalTableauNode> { fs, le -> PropositionalTableauNode(fs, le) }
    }

    fun <T : PropositionalTableauNode> applyBeta(nodeFactory: (MutableList<SignedFormula<*>>, T) -> T): Boolean {
        var beta: BetaFormula<Formula<*>>? = null
        val node = root.breadthFirst<T> {
            beta = it.newFormulas.firstOrNull { it is BetaFormula<*> } as BetaFormula<*>?
            beta !== null
        }
        if (node === null)
            return false
        beta?.let {
            beta ->
            node.newFormulas.remove(beta);
            val leaves = node.allLeaves<T>()
            leaves.map { leaf -> beta.generateChildren().map { leaf.children.add(nodeFactory(mutableListOf(it), leaf)) } }
            return true
        } ?: return false
    }


}

open class FolTableau(root: FolTableauNode) : PropositionalTableau<FolTableauNode>(root) {

    private fun createInitialSubstitutions() {
//        root.dep { node ->
//        true
//        }

    }

    fun unify(): MultiBranchCloser? {
        root.breadthFirst<FolTableauNode> { node ->
            node.toString()
            true
        }
        return null
    }

    override fun step(): Boolean =
            if (!applyDelta() && !applyBeta<FolTableauNode> { fs, le -> FolTableauNode(fs, le) }) {
                applyGamma()
            } else {
                true
            }

    // TODO make this splice
    private fun applyDelta(): Boolean {
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
                    leaf.children.add(FolTableauNode(mutableListOf(it), leaf))
                }
            }
            return true
        } ?: return false
    }

    // TODO make this splice
    private fun applyGamma(): Boolean {
        var gammas: MutableSet<Pair<GammaFormula<*>, FolTableauNode>> = java.util.TreeSet<Pair<GammaFormula<*>, FolTableauNode>>(Comparator<Pair<GammaFormula<*>, FolTableauNode>> {
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
                    leaf.children.add(FolTableauNode(mutableListOf(it), leaf))
                }
            }
            return true
        } ?: return false
    }

}
