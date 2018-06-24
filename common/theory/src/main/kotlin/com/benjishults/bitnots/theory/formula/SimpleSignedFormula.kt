package com.benjishults.bitnots.theory.formula

import com.benjishults.bitnots.model.formulas.Formula

abstract class SimpleSignedFormula<F : Formula<*>>(formula: F, sign: Boolean)
    : SignedFormula<F>(formula, sign) {
    override fun generateChildren(): List<SignedFormula<Formula<*>>> = emptyList()
}
