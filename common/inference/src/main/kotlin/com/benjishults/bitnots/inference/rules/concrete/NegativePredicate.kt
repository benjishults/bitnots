package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.inference.rules.NilOpFormula

class NegativePredicate(formula: Predicate) : NilOpFormula<Predicate>(formula, false)
