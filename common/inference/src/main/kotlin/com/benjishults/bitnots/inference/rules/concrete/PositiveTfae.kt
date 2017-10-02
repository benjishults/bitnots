package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Tfae

class PositiveTfae(tfae: Tfae) : BetaFormula<Tfae>(tfae, true) {
	override fun generateChildren() =
			listOf(PositiveAnd(And(*formula.formulas)),
					PositiveAnd(And(*formula.formulas.map { Not(it) }.toTypedArray())))
}
