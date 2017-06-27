package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor

class And(vararg conjuncts: Formula): VarArgPropositionalFormula(FormulaConstructor.intern(LogicalOperators.and.name), *conjuncts)
