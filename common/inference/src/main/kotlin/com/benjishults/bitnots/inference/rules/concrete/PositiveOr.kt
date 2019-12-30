package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.propositional.Or

class PositiveOr(or: Or) : BetaFormula<Or>(or, true) {
	override fun generateChildren() = formula.formulas.map { it.createSignedFormula(true) }
}
