package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.tableau.strategy.PropositionalInitializationStrategy

open class PropositionalTableau(
        override val root: PropositionalTableauNode
) : Tableau<PropositionalTableauNode> {

    constructor(formula: Formula<*>) : this(
            PropositionalInitializationStrategy.init(
                    PropositionalTableauNode(
                            mutableListOf(formula.createSignedFormula()))))

    // TODO how different is this to FolTableau?
    override fun toString(): String {
        return buildString {
            root.preOrderWithPath { n, path ->
                this.append(path.joinToString("."))
                this.append("\n")
                this.append(n.toString())
                //                this.append(n.initialClosers.toString())
                this.append("\n")
                this.append("\n")
                false
            }
        }
    }

}
