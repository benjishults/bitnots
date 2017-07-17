package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.inference.AlphaFormula
import com.benjishults.bitnots.model.inference.SignedFormula
import com.benjishults.bitnots.model.inference.createSignedFormula

class NegativeImplies(implies: Implies) : AlphaFormula<Implies>(implies, false) {
	override fun generateChildren(): List<SignedFormula<out Formula>> =
			listOf(formula.antecedent.createSignedFormula(true), formula.consequent.createSignedFormula(false))
}