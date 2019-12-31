package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.rules.DeltaFormula
import com.benjishults.bitnots.inference.rules.GammaFormula
import com.benjishults.bitnots.model.formulas.fol.VarsBindingFormula
import com.benjishults.bitnots.tableau.FolTableauNode
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.TableauNode
import java.util.PriorityQueue
import kotlin.Comparator

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
            var deltaMaybeNull: DeltaFormula<out VarsBindingFormula>? = null
            val node = root.breadthFirst<FolTableauNode> {
                deltaMaybeNull = it.newFormulas.firstOrNull { it is DeltaFormula<*> } as DeltaFormula<*>?
                deltaMaybeNull !== null
            }
            if (node === null)
                return false
            deltaMaybeNull?.let { delta ->
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
