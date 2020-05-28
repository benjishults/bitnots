package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.propositional.Not

class PositiveNot(
    override val formula: Not
) : AlphaFormula<Not>, PositiveSignedFormula<Not>, AbsractSignedFormula<Not>() {
    override fun generateChildren() = listOf(formula.argument.createSignedFormula(false))
}
