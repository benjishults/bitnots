package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.propositional.Truth
import com.benjishults.bitnots.inference.rules.NilOpFormula

object PositiveTruth : NilOpFormula<Truth>(Truth, true) 
