package com.benjishults.bitnots.model.formulas.fol

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable

abstract class VarBindingFormula(cons: FormulaConstructor, val formula: Formula, vararg val variables: BoundVariable) : Formula(cons) {
	override fun getFreeVariables(): Set<FreeVariable> = formula.getFreeVariables()

	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> {
		return formula.getVariablesUnboundExcept(boundVars.plus(variables))
	}

	override fun substitute(map: Map<Variable, Term>): Formula {
		return this::class.constructors.first().call(formula.substitute(map), variables)
	}

	override fun toString(): String = "(${constructor.name} (${variables.joinToString(" ")}) ${formula})"
}