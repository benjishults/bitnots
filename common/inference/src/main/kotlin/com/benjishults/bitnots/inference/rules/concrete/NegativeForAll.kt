package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.inference.rules.DeltaFormula

class NegativeForAll(formula: ForAll) : DeltaFormula<ForAll>(formula, false)
