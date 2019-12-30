package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.propositional.Implies

class PositiveImplies(implies: Implies) : BetaFormula<Implies>(implies, true) {
	override fun generateChildren() =
			listOf(formula.antecedent.createSignedFormula(false), formula.consequent.createSignedFormula(true))
}
