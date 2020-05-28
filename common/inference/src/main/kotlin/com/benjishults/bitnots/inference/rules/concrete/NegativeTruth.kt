package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.model.formulas.propositional.Truth

object NegativeTruth : ClosingFormula<Truth>, NegativeSignedFormula<Truth>, AbsractSignedFormula<Truth>() {
    override val formula = Truth
}
