package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.inference.BetaFormula
import com.benjishults.bitnots.model.inference.SignedFormula

class PositiveIff(iff: Iff) : BetaFormula<Iff>(iff, true) {
	override fun generateChildren(): List<SignedFormula<out Formula>> =
			listOf(PositiveAnd(And(formula.first, formula.second)),
					PositiveAnd(And(Not(formula.first), Not(formula.second))))
}