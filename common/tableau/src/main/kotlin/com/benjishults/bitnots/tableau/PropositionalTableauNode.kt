package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.tableau.strategy.InitializingStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalInitializationStrategy
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula

class PropositionalTableauNode(
        newFormulas: MutableList<SignedFormula<Formula<*>>> = mutableListOf(),
        parent: PropositionalTableauNode? = null,
        init: InitializingStrategy = PropositionalInitializationStrategy()
) : AbstractTableauNode(newFormulas, parent, init)
