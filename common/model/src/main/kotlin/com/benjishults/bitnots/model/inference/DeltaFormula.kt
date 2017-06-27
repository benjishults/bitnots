package com.benjishults.bitnots.model.inference

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.model.terms.Variable

abstract class DeltaFormula<F : VarBindingFormula>(formula: F, sign: Boolean) : SignedFormula<F>(formula, sign) {
	override fun generateChildren(): List<SignedFormula<out Formula>> {
		val unboundVars = formula.formula.getFreeVariables()
		val skolems = formula.variables.fold(mutableMapOf<Variable, Function>()) { s, t -> s.also { it.put(t, Function.new(t.cons.name, *unboundVars.toTypedArray())) } }
		formula.variables.map {
			skolems.put(it, Function.new(it.cons.name, *unboundVars.toTypedArray()))
		}
		return listOf(formula.formula.substitute(skolems).createSignedFormula(sign))
	}
}
