package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.rules.GammaFormula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.util.Counter

data class PositiveForAll(
    override val formula: ForAll
) : GammaFormula<ForAll>, PositiveSignedFormula<ForAll>, Counter by Counter(), AbsractSignedFormula<ForAll>()
