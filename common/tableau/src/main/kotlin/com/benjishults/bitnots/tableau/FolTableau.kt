package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.util.StepCounter
import com.benjishults.bitnots.util.StepCounterImpl
import com.benjishults.bitnots.tableau.strategy.PropositionalInitializationStrategy

class FolTableau(
    override val root: FolTableauNode
) : Tableau<FolTableauNode>(),
    StepCounter by StepCounterImpl() {

    constructor(formula: Formula) : this(
        PropositionalInitializationStrategy.init(
            FolTableauNode(mutableListOf(formula.createSignedFormula()))
        )
    )

    override fun toString() = ""/*: String {
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
    }*/

}
