package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.propositional.And

class PositiveAnd(
    override val formula: And
) : AlphaFormula<And>, PositiveSignedFormula<And>, AbsractSignedFormula<And>() {
    override fun generateChildren() = formula.formulas.map { it.createSignedFormula(true) }
}
