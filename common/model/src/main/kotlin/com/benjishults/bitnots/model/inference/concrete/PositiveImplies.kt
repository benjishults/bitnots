package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.inference.BetaFormula
import com.benjishults.bitnots.model.inference.SignedFormula
import com.benjishults.bitnots.model.inference.createSignedFormula

class PositiveImplies(implies: Implies) : BetaFormula<Implies>(implies, true) {
	override fun generateChildren(): List<SignedFormula<out Formula>> =
			listOf(formula.antecedent.createSignedFormula(false), formula.consequent.createSignedFormula(true))
}