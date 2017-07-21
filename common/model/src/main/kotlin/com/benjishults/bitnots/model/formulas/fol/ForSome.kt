package com.benjishults.bitnots.model.formulas.fol

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.formulas.propositional.LogicalOperators
import com.benjishults.bitnots.model.terms.BoundVariable


class ForSome(formula: Formula<*>, vararg variables: BoundVariable) :
		VarBindingFormula(FormulaConstructor.intern(LogicalOperators.`for-some`.name), formula, *variables)
