package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula

interface StepStrategy<in T : Tableau> {
    fun step(tableau: T): Boolean
}

open class PropositionalStepStrategy<N : TableauNode>(
        val nodeFactory: (MutableList<SignedFormula<*>>, N) -> N
) : StepStrategy<Tableau> {
    override open fun step(tableau: Tableau): Boolean =
            with(tableau) {
                applyBeta(tableau)
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
