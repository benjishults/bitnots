package com.benjishults.bitnots.model.formulas.fol

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.formulas.propositional.LogicalOperators
import com.benjishults.bitnots.model.terms.BoundVariable


class ForAll(formula: Formula<*>, vararg variables: BoundVariable) :
		VarBindingFormula(FormulaConstructor.intern(LogicalOperators.`for-all`.name), formula, *variables)
