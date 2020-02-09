package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.PropositionalFormulaConstructor

class And(vararg conjuncts: Formula): VarArgPropositionalFormula(
        PropositionalFormulaConstructor.intern(LogicalOperator.and.name), *conjuncts)
