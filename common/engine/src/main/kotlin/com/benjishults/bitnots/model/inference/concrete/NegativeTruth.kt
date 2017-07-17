package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.propositional.Truth
import com.benjishults.bitnots.model.inference.ClosingFormula

object NegativeTruth : ClosingFormula<Truth>(Truth, false)
