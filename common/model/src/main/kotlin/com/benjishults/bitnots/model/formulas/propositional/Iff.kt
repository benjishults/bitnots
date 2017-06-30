package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.Substitution

class Iff(val first: Formula, val second: Formula) : PropositionalFormula(FormulaConstructor.intern(LogicalOperators.iff.name)) {
    override fun unify(other: Formula, sub: Substitution): Substitution {
        TODO()
    }

    override fun applySub(substitution: Substitution): Formula {
        TODO()
    }

	override fun getFreeVariables(): Set<FreeVariable> = first.getFreeVariables().union(second.getFreeVariables())

	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> =
			first.getVariablesUnboundExcept(boundVars).union(second.getVariablesUnboundExcept(boundVars))

//	override fun applySub(substitution: Substitution): Iff {
//		return Iff(first.applySub(substitution), second.applySub(substitution))
//	}

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
