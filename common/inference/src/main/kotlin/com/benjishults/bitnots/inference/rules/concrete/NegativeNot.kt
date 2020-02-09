package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Not

class NegativeNot(not: Not) : AlphaFormula<Not>(not, false) {
	override fun generateChildren(): List<SignedFormula<Formula>> = listOf(formula.argument.createSignedFormula(true))
}
