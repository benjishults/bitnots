package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.inference.AlphaFormula
import com.benjishults.bitnots.model.inference.SignedFormula
import com.benjishults.bitnots.model.inference.createSignedFormula

class NegativeOr(or: Or) : AlphaFormula<Or>(or, false) {
	override fun generateChildren(): List<SignedFormula<out Formula>> = formula.formulas.map { it.createSignedFormula(false) }
}