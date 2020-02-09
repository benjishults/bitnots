package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Implies


class NegativeIff(iff: Iff) : BetaFormula<Iff>(iff, false) {
	override fun generateChildren(): List<SignedFormula<Formula>> =
			listOf(NegativeImplies(Implies(formula.first, formula.second)),
					NegativeImplies(Implies(formula.second, formula.first)))
}
