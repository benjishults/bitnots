package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.propositional.Not

class PositiveNot(not: Not) : AlphaFormula<Not>(not, true) {
	override fun generateChildren() = listOf(formula.argument.createSignedFormula(false))
}
