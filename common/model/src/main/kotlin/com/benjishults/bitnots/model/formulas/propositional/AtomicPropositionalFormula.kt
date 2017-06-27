package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable

abstract class AtomicPropositionalFormula(cons: FormulaConstructor) : PropositionalFormula(cons) {
	override fun getFreeVariables(): Set<FreeVariable> = emptySet()

	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> = emptySet()

	override fun equals(other: Any?): Boolean = other === this
	override fun substitute(map: Map<Variable, Term>) = this
}