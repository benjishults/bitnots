package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Substitution

class Not(val argument: Formula) : PropositionalFormula(FormulaConstructor.intern(LogicalOperators.not.name)) {
	override fun unify(other: Formula, sub: Substitution): Substitution = if (other is Not) argument.unify(other.argument, sub) else NotUnifiable

	override fun getFreeVariables(): Set<FreeVariable> = argument.getFreeVariables()

	override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> = argument.getVariablesUnboundExcept(boundVars)

	override fun applySub(substitution: Substitution): Not {
		return Not(argument.applySub(substitution))
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
