package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.FolTableauNode
import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.closer.UnifyingClosedIndicator
import com.benjishults.bitnots.inference.rules.DeltaFormula
import com.benjishults.bitnots.inference.rules.GammaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula

import java.util.TreeSet

open class FolStepStrategy(
        var qLimit: Int = 3,
        nodeFactory: (MutableList<SignedFormula<*>>, FolTableauNode) -> FolTableauNode
) : PropositionalStepStrategy<FolTableauNode>(nodeFactory) {

    override open fun step(tableau: Tableau): Boolean =
            with(tableau) {
                var taken = false
                while (applyDelta(tableau)) {
                    taken = true
                }
                if (!taken)
                    applyBeta(tableau) || applyGamma(tableau)
                else
                    true
            }

    // TODO make this splice
    private fun applyDelta(tableau: Tableau): Boolean {
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
    private fun applyGamma(tableau: Tableau): Boolean {
        with(tableau) {
            var gammas: MutableSet<Pair<GammaFormula<*>, FolTableauNode>> =
                    TreeSet<Pair<GammaFormula<*>, FolTableauNode>>(
                            Comparator<Pair<GammaFormula<*>, FolTableauNode>> {
                                o1: Pair<GammaFormula<*>, FolTableauNode>?, o2: Pair<GammaFormula<*>, FolTableauNode>? ->
                                o1!!.first.numberOfApplications.compareTo(o2!!.first.numberOfApplications)
                            })
            root.breadthFirst<FolTableauNode> { node ->
                gammas.addAll(node.newFormulas.filterIsInstance<GammaFormula<*>>().filter {
                    it.numberOfApplications <= qLimit
                }.map {
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
