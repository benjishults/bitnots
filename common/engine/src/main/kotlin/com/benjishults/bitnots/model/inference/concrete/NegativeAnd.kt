package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.inference.BetaFormula
import com.benjishults.bitnots.model.inference.SignedFormula
import com.benjishults.bitnots.model.inference.createSignedFormula

class NegativeAnd(and: And) : BetaFormula<And>(and, false) {
	override fun generateChildren(): List<SignedFormula<out Formula>> = formula.formulas.map {it.createSignedFormula(false)}
}
