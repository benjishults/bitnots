package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.inference.GammaFormula
import com.benjishults.bitnots.model.inference.SignedFormula

class NegativeForAll(formula: ForAll) : GammaFormula<ForAll>(formula, true)
