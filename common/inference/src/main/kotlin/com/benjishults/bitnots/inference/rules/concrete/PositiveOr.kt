package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.createSignedFormula

class PositiveOr(or: Or) : BetaFormula<Or>(or, true) {
	override fun generateChildren() = formula.formulas.map { it.createSignedFormula(true) }
}