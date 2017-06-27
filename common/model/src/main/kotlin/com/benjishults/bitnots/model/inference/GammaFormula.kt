package com.benjishults.bitnots.model.inference

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.terms.FV

abstract class GammaFormula<F : VarBindingFormula>(formula: F, sign: Boolean) : SignedFormula<F>(formula, sign) {
	override fun generateChildren(): List<SignedFormula<out Formula>> {
		val boundToFree = mutableMapOf<Variable, Variable>()
		formula.variables.map {
			boundToFree.put(it,
					if (FreeVariable.exists(it.cons.name))
						FreeVariable.new(it.cons.name)
					else
						FV(it.cons.name))
		}
		return listOf(formula.formula.substitute(boundToFree).createSignedFormula(sign))
	}
}
