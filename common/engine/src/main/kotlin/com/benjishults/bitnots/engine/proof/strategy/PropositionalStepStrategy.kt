package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula

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
