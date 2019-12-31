package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor

class Or(vararg disjuncts: Formula<*>): VarArgPropositionalFormula(FormulaConstructor.intern(LogicalOperator.or.name), *disjuncts)
