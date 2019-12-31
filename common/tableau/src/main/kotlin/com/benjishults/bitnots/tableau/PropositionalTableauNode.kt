package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.prover.strategy.InitializationStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalInitializationStrategy

class PropositionalTableauNode(
        newFormulas: MutableList<SignedFormula<Formula<*>>> = mutableListOf(),
        parent: PropositionalTableauNode? = null,
        init: InitializationStrategy<TableauNode> = PropositionalInitializationStrategy()
) : AbstractTableauNode(newFormulas, parent, init)
