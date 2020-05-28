package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.rules.DeltaFormula
import com.benjishults.bitnots.model.formulas.fol.ForAll

class NegativeForAll(override val formula: ForAll) : DeltaFormula<ForAll>, NegativeSignedFormula<ForAll>, AbsractSignedFormula<ForAll>()
