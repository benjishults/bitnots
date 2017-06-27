package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.inference.DeltaFormula
import com.benjishults.bitnots.model.inference.SignedFormula

class PositiveForAll(formula: ForAll) : DeltaFormula<ForAll>(formula, true)
