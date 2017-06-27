package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.inference.DeltaFormula
import com.benjishults.bitnots.model.inference.SignedFormula

class NegativeForSome(formula: ForSome) : DeltaFormula<ForSome>(formula, true)
