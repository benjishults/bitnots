package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.inference.AlphaFormula
import com.benjishults.bitnots.model.inference.SignedFormula
import com.benjishults.bitnots.model.inference.createSignedFormula

class NegativeNot(not: Not) : AlphaFormula<Not>(not, false) {
	override fun generateChildren(): List<SignedFormula<out Formula>> = listOf(formula.argument.createSignedFormula(true))
}