package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.TableauNode

open class PropositionalStepStrategy(
        val nodeFactory: (MutableList<SignedFormula<*>>, TableauNode) -> TableauNode
) : StepStrategy<Tableau> {
    override open fun step(tableau: Tableau): Boolean =
            with(tableau) {
                applyBeta(tableau)
            }

    protected fun addChildFormulasToNewLeaves(signed: SignedFormula<*>, node: TableauNode) {
        signed.generateChildren().let { childFormulas ->
            node.allLeaves<TableauNode>().forEach { leaf ->
                childFormulas.forEach {
                    leaf.children.add(nodeFactory(mutableListOf(it), leaf))
                    Unit
                }
            }
        }
    }

    fun applyBeta(tableau: Tableau): Boolean =
            with(tableau) {
                var betaMaybeNull: BetaFormula<Formula<*>>? = null
                val node = root.breadthFirst<TableauNode> {
                    betaMaybeNull = it.newFormulas.firstOrNull { it is BetaFormula<*> } as BetaFormula<*>?
                    betaMaybeNull !== null
                }
                if (node === null)
                    false
                else
                    betaMaybeNull?.let { beta ->
                        node.newFormulas.remove(beta);
                        addChildFormulasToNewLeaves(beta, node)
                        true
                    } ?: false
            }

}
