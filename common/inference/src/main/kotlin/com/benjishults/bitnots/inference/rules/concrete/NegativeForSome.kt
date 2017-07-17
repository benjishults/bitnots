package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.inference.rules.GammaFormula

class NegativeForSome(formula: ForSome) : GammaFormula<ForSome>(formula, false)
