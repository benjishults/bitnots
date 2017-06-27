package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.inference.GammaFormula
import com.benjishults.bitnots.model.inference.SignedFormula

class PositiveForSome(formula: ForSome) : GammaFormula<ForSome>(formula, true)
