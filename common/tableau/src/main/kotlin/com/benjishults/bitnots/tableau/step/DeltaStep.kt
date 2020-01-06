package com.benjishults.bitnots.tableau.step

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.rules.DeltaFormula
import com.benjishults.bitnots.model.formulas.fol.VarsBindingFormula
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.TableauNode

class DeltaStep<T : Tableau<N>, N : TableauNode<N>>(
        override val nodeFactory: (SignedFormula<*>, N) -> N
) : Step<T>,
    TableauStep<N> {

    // TODO make this splice
    override fun apply(pip: T): Boolean =
            with(pip) {
                var deltaMaybeNull: DeltaFormula<out VarsBindingFormula>? = null
                val node = root.breadthFirst { node ->
                    deltaMaybeNull = node.newFormulas.firstOrNull { it is DeltaFormula<*> } as DeltaFormula<*>?
                    deltaMaybeNull !== null
                }
                if (node === null)
                    return false
                deltaMaybeNull?.let { delta ->
                    node.newFormulas.remove(delta)
                    addChildFormulasToNewLeaves(delta, node)
                    return true
                } ?: return false
            }

}
