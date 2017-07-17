package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.inference.DeltaFormula

class NegativeForAll(formula: ForAll) : DeltaFormula<ForAll>(formula, false)
