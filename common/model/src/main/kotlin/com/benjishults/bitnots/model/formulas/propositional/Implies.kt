package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.Substitution

class Implies(val antecedent: Formula, val consequent: Formula) : PropositionalFormula(FormulaConstructor.intern(LogicalOperators.implies.name)) {
	override fun unify(other: Formula): Substitution? =
			if (other is Implies)
				antecedent.unify(other.antecedent)?.let { return it.compose(consequent.unify(other.consequent)) }
			else
				null

	override fun getFreeVariables(): Set<FreeVariable> = antecedent.getFreeVariables().union(consequent.getFreeVariables())

	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> =
			antecedent.getVariablesUnboundExcept(boundVars).union(consequent.getVariablesUnboundExcept(boundVars))

	override fun applySub(substitution: Substitution): Implies {
		return Implies(antecedent.applySub(substitution), consequent.applySub(substitution))
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
