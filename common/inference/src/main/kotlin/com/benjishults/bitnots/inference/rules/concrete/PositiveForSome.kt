package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.inference.rules.DeltaFormula

class PositiveForSome(formula: ForSome) : DeltaFormula<ForSome>(formula, true)
