package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.theory.formula.createSignedFormula

class PositiveImplies(implies: Implies) : BetaFormula<Implies>(implies, true) {
	override fun generateChildren() =
			listOf(formula.antecedent.createSignedFormula(false), formula.consequent.createSignedFormula(true))
}