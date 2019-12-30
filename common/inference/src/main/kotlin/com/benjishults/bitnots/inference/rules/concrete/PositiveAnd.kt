package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.propositional.And

class PositiveAnd(and: And) : AlphaFormula<And>(and, true) {
	override fun generateChildren() = formula.formulas.map { it.createSignedFormula(true) }
}
