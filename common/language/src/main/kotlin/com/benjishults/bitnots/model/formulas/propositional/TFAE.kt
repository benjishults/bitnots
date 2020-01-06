package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.PropositionalFormulaConstructor

class Tfae(
        vararg arguments: Formula<*>
) : VarArgPropositionalFormula(PropositionalFormulaConstructor.intern(LogicalOperator.TFAE.name), *arguments) {
    init {
        require(arguments.size > 2) { "To express that two formulas are equivalent, use Iff rather than Tfae." }
    }
}
