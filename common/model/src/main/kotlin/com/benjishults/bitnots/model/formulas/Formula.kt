package com.benjishults.bitnots.model.formulas

import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.Substitution

abstract class Formula(val constructor: FormulaConstructor) {
	abstract fun unify(other: Formula): Substitution?
	abstract fun applySub(substitution: Substitution): Formula

	abstract fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable>
	abstract fun getFreeVariables(): Set<FreeVariable>

	override fun toString() = "(${constructor.name})"
}