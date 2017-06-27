package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.propositional.Truth
import com.benjishults.bitnots.model.inference.NilOpFormula

object PositiveTruth : NilOpFormula<Truth>(Truth, true) 
