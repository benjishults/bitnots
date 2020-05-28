package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.propositional.Or

class NegativeOr(
    override val formula: Or
) : AlphaFormula<Or>, NegativeSignedFormula<Or>, AbsractSignedFormula<Or>() {
    override fun generateChildren() = formula.formulas.map { it.createSignedFormula(false) }
}
