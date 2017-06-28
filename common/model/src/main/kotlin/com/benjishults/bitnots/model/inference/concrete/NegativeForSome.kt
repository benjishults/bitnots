package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.inference.GammaFormula

class NegativeForSome(formula: ForSome) : GammaFormula<ForSome>(formula, false)
