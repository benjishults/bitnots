package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.propositional.Or

class NegativeOr(or: Or) : AlphaFormula<Or>(or, false) {
	override fun generateChildren() = formula.formulas.map { it.createSignedFormula(false) }
}
