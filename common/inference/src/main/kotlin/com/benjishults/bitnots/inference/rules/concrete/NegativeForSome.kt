package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.rules.GammaFormula
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.util.Counter

data class NegativeForSome(
    override val formula: ForSome
) : GammaFormula<ForSome>, NegativeSignedFormula<ForSome>, Counter by Counter(), AbsractSignedFormula<ForSome>()
