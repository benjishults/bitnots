package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.rules.DeltaFormula
import com.benjishults.bitnots.model.formulas.fol.ForSome

data class PositiveForSome(
    override val formula: ForSome
) : DeltaFormula<ForSome>, PositiveSignedFormula<ForSome>, AbsractSignedFormula<ForSome>()
