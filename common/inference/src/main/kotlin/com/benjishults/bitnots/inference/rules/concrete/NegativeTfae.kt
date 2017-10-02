package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Tfae

class NegativeTfae(tfae: Tfae) : AlphaFormula<Tfae>(tfae, false) {
	override fun generateChildren() =
			listOf(NegativeAnd(And(*formula.formulas)),
					NegativeAnd(And(*formula.formulas.map { Not(it) }.toTypedArray())))
}