package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.rules.NilOpFormula
import com.benjishults.bitnots.model.formulas.propositional.Truth

object PositiveTruth : NilOpFormula<Truth>, PositiveSignedFormula<Truth>, AbsractSignedFormula<Truth>() {
    override val formula = Truth
    override fun hashCode(): Int = Truth.hashCode()
}
