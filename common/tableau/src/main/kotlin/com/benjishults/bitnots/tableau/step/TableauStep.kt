package com.benjishults.bitnots.tableau.step

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.theory.Theory

interface TableauStep<N : TableauNode<N>> {

    val nodeFactory: (SignedFormula<*>, N) -> N

    fun addChildFormulasToNewLeaves(
        signed: SignedFormula<*>, node: N,
        theory: Theory = Theory
    ) {
        signed.generateChildren(theory).let { childFormulas ->
            node.allLeaves().forEach { leaf ->
                childFormulas.forEach {
                    leaf.children.add(nodeFactory(it, leaf))
                }
            }
        }
    }

}
