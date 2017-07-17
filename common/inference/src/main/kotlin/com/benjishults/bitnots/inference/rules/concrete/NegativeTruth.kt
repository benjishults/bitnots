package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.propositional.Truth
import com.benjishults.bitnots.inference.rules.ClosingFormula

object NegativeTruth : ClosingFormula<Truth>(Truth, false)
