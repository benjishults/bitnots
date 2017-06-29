package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.Substitution

abstract class AtomicPropositionalFormula(cons: FormulaConstructor) : PropositionalFormula(cons) {

	override fun unify(other: Formula): Substitution? = if (this == other) Substitution() else null
	override fun applySub(substitution: Substitution): Formula = this


	override fun getFreeVariables(): Set<FreeVariable> = emptySet()
	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> = emptySet()

	override fun equals(other: Any?): Boolean = other === this
}