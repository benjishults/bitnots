package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.propositional.Falsity
import com.benjishults.bitnots.model.inference.NilOpFormula

object NegativeFalsity : NilOpFormula<Falsity>(Falsity, false)
