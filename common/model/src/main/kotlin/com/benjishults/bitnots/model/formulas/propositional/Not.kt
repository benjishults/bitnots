package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable

class Not(val argument: Formula) : PropositionalFormula(FormulaConstructor.intern(LogicalOperators.not.name)) {
	override fun getFreeVariables(): Set<FreeVariable> = argument.getFreeVariables()

	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> = argument.getVariablesUnboundExcept(boundVars)

	override fun substitute(map: Map<Variable, Term>): Not {
		return Not(argument.substitute(map))
	}
	override fun equals(other: Any?): Boolean {
		if (other === null) return false
		if (other::class === this::class) {
			return (other as Not).argument == argument
		}
		return false
	}

	override fun toString() = "(${constructor.name} ${argument})"
}
