package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Not

class NegativeNot(
    override val formula: Not
) : AlphaFormula<Not>, NegativeSignedFormula<Not>, AbsractSignedFormula<Not>() {
    override fun generateChildren(): List<SignedFormula<Formula>> = listOf(formula.argument.createSignedFormula(true))
}
