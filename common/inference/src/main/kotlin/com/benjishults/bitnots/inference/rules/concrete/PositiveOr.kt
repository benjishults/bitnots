package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.propositional.Or

class PositiveOr(
    override val formula: Or
) : BetaFormula<Or>, PositiveSignedFormula<Or>, AbsractSignedFormula<Or>() {
    override fun generateChildren() = formula.formulas.map { it.createSignedFormula(true) }
}
