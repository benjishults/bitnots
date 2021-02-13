package com.benjishults.bitnots.inference

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.AtomicFormula
import com.benjishults.bitnots.theory.Theory

interface SimpleSignedFormula<F : AtomicFormula> : SignedFormula<F> {
	override fun generateChildren(theory: Theory): List<SignedFormula<Formula>> = emptyList()
}
