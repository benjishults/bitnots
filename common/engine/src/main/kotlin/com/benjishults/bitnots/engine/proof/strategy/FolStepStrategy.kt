package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.FolTableauNode
import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.inference.rules.DeltaFormula
import com.benjishults.bitnots.inference.rules.GammaFormula
import com.benjishults.bitnots.model.formulas.fol.VarsBindingFormula
import com.benjishults.bitnots.theory.formula.SignedFormula
import java.util.PriorityQueue

open class FolStepStrategy(
        var qLimit: Int = 3,
        nodeFactory: (MutableList<SignedFormula<*>>, TableauNode) -> TableauNode
) : PropositionalStepStrategy(nodeFactory) {

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
            var delta: DeltaFormula<out VarsBindingFormula>? = null
            val node = root.breadthFirst<FolTableauNode> {
                delta = it.newFormulas.firstOrNull { it is DeltaFormula<*> } as DeltaFormula<*>?
                delta !== null
            }
            if (node === null)
                return false
            delta?.let { delta ->
                node.newFormulas.remove(delta);
                addChildFormulasToNewLeaves(delta, node)
                return true
            } ?: return false
        }
    }

    // TODO make this splice
    private fun applyGamma(tableau: Tableau): Boolean {
        with(tableau) {
            var gammas: PriorityQueue<Pair<GammaFormula<*>, FolTableauNode>> =
                    PriorityQueue<Pair<GammaFormula<*>, FolTableauNode>>(
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
            gammas.firstOrNull()?.let { (gamma, node) ->
                gamma.numberOfApplications++
                addChildFormulasToNewLeaves(gamma, node)
                return true
            } ?: return false
        }
    }

}
