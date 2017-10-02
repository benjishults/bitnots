package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.theory.formula.createSignedFormula

class PositiveAnd(and: And) : AlphaFormula<And>(and, true) {
	override fun generateChildren() = formula.formulas.map { it.createSignedFormula(true) }
}
