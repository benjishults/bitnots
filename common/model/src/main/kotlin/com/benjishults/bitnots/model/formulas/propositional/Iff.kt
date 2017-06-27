package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable


class Iff(val first: Formula, val second: Formula) : PropositionalFormula(FormulaConstructor.intern(LogicalOperators.iff.name)) {
	override fun getFreeVariables(): Set<FreeVariable> = first.getFreeVariables().union(second.getFreeVariables())

	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> =
			first.getVariablesUnboundExcept(boundVars).union(second.getVariablesUnboundExcept(boundVars))

	override fun substitute(map: Map<Variable, Term>): Iff {
		return Iff(first.substitute(map), second.substitute(map))
	}

	override fun toString(): String = "(${constructor.name} ${first} ${second})"
	override fun equals(other: Any?): Boolean {
		if (other === null) return false
		if (other::class === this::class) {
			return ((other as Iff).first == first && other.second == second) ||
					(other.second == first && other.first == second)
		}
		return false
	}
}
