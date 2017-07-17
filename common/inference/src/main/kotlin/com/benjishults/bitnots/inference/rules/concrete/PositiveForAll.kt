package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.inference.rules.GammaFormula

class PositiveForAll(formula: ForAll) : GammaFormula<ForAll>(formula, true)
