package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.createSignedFormula

class NegativeImplies(implies: Implies) : AlphaFormula<Implies>(implies, false) {
	override fun generateChildren(): List<SignedFormula<out Formula>> =
			listOf(formula.antecedent.createSignedFormula(true), formula.consequent.createSignedFormula(false))
}