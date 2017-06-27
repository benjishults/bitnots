package com.benjishults.bitnots.model.formulas

import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable

abstract class Formula(val constructor: FormulaConstructor) {
	abstract fun substitute(map: Map<Variable, Term>): Formula
	abstract fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable>
	abstract fun getFreeVariables(): Set<FreeVariable>
	override fun toString() = "(${constructor.name})"
}