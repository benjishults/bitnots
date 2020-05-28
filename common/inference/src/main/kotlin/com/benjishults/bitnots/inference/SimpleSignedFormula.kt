package com.benjishults.bitnots.inference

import com.benjishults.bitnots.model.formulas.Formula

interface SimpleSignedFormula<F : Formula> : SignedFormula<F> {
	override fun generateChildren(): List<SignedFormula<Formula>> = emptyList()
}
