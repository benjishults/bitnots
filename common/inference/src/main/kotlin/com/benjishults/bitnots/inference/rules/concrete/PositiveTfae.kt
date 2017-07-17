package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Tfae
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula

class PositiveTfae(tfae: Tfae) : BetaFormula<Tfae>(tfae, false) {
	override fun generateChildren(): List<SignedFormula<out Formula>> =
			listOf(PositiveAnd(And(*formula.formulas)),
					PositiveAnd(And(*formula.formulas.map { Not(it) }.toTypedArray())))
}
