package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.inference.GammaFormula

class PositiveForAll(formula: ForAll) : GammaFormula<ForAll>(formula, true)
