package com.benjishults.bitnots.tableau.step

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.TableauNode

class BetaStep<T : Tableau<N>, N : TableauNode<N>>(
        override val nodeFactory: (SignedFormula<*>, N) -> N
) : Step<T>,
    TableauStep<N> {

    override fun apply(pip: T): Boolean {
        var betaMaybeNull: BetaFormula<Formula<*>>? = null
        val node = pip.root.breadthFirst {
            betaMaybeNull = it.newFormulas.firstOrNull { it is BetaFormula<*> } as BetaFormula<*>?
            betaMaybeNull !== null
        }
        return if (node === null)
            false
        else
            betaMaybeNull?.let { beta ->
                node.newFormulas.remove(beta);
                addChildFormulasToNewLeaves(beta, node)
                true
            } ?: false
    }
}
