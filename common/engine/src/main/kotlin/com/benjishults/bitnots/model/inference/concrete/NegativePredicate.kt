package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.inference.NilOpFormula

class NegativePredicate(formula: Predicate) : NilOpFormula<Predicate>(formula, false)
