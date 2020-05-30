package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.model.formulas.propositional.Falsity

object PositiveFalsity
    : ClosingFormula<Falsity>,
      PositiveSignedFormula<Falsity>,
      AbsractSignedFormula<Falsity>() {
    override val formula = Falsity
    override fun hashCode(): Int = Falsity.hashCode()
}
