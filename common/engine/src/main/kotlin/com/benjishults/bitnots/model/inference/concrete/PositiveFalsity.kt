package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.propositional.Falsity
import com.benjishults.bitnots.model.inference.ClosingFormula

object PositiveFalsity: ClosingFormula<Falsity>(Falsity, true)
