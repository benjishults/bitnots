package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.createSignedFormula

class PositiveImplies(implies: Implies) : BetaFormula<Implies>(implies, true) {
	override fun generateChildren(): List<SignedFormula<out Formula>> =
			listOf(formula.antecedent.createSignedFormula(false), formula.consequent.createSignedFormula(true))
}