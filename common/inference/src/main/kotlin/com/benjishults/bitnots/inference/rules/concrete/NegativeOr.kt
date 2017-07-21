package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.createSignedFormula

class NegativeOr(or: Or) : AlphaFormula<Or>(or, false) {
	override fun generateChildren(): List<SignedFormula<out Formula<*>>> = formula.formulas.map { it.createSignedFormula(false) }
}