package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.propositional.Falsity
import com.benjishults.bitnots.inference.rules.NilOpFormula

object NegativeFalsity : NilOpFormula<Falsity>(Falsity, false)
