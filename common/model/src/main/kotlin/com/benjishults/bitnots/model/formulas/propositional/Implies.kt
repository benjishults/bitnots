package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable

class Implies(val antecedent: Formula, val consequent: Formula) : PropositionalFormula(FormulaConstructor.intern(LogicalOperators.implies.name)) {
	override fun getFreeVariables(): Set<FreeVariable> = antecedent.getFreeVariables().union(consequent.getFreeVariables())

	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> =
			antecedent.getVariablesUnboundExcept(boundVars).union(consequent.getVariablesUnboundExcept(boundVars))

	override fun substitute(map: Map<Variable, Term>): Implies {
		return Implies(antecedent.substitute(map), consequent.substitute(map))
	}

	override fun toString() = "(${constructor.name} ${antecedent} ${consequent})"
	override fun equals(other: Any?): Boolean {
		if (other === null) return false
		if (other::class === this::class) {
			return (other as Implies).antecedent == antecedent && other.consequent == consequent
		}
		return false
	}

}
