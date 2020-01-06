package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.PropositionalFormulaConstructor

class Or(vararg disjuncts: Formula<*>): VarArgPropositionalFormula(
        PropositionalFormulaConstructor.intern(LogicalOperator.or.name), *disjuncts)
