package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.theory.formula.SignedFormula

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
                        addChildFormulasToNewLeaves(beta, node)
                        true
                    } ?: false
            }

}
