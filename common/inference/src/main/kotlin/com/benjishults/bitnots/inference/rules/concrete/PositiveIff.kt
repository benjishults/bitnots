package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Not

class PositiveIff(iff: Iff) : BetaFormula<Iff>(iff, true) {
	override fun generateChildren() =
			listOf(PositiveAnd(And(formula.first, formula.second)),
					PositiveAnd(And(Not(formula.first), Not(formula.second))))
}
