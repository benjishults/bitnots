package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.inference.BetaFormula
import com.benjishults.bitnots.model.inference.SignedFormula


class NegativeIff(iff: Iff) : BetaFormula<Iff>(iff, false) {
	override fun generateChildren(): List<SignedFormula<out Formula>> =
			listOf(NegativeImplies(Implies(formula.first, formula.second)),
					NegativeImplies(Implies(formula.second, formula.first)))
}