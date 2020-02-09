package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula

class PropositionalTableauNode(
        newFormulas: MutableList<SignedFormula<Formula>> = mutableListOf(),
        parent: PropositionalTableauNode? = null
) : TableauNode<PropositionalTableauNode>(newFormulas, parent)
