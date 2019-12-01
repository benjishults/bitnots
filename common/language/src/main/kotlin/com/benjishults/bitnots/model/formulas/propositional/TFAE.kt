package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor

class Tfae(
        vararg arguments: Formula<*>
) : VarArgPropositionalFormula(FormulaConstructor.intern(LogicalOperators.TFAE.name), *arguments) {
    init {
        require(arguments.size > 2) { "To express that two formulas are equivalent, use Iff rather than Tfae." }
    }
}
