package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.inference.DeltaFormula

class PositiveForSome(formula: ForSome) : DeltaFormula<ForSome>(formula, true)
