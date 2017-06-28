package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.inference.NilOpFormula

class PositivePredicate(formula: Predicate) : NilOpFormula<Predicate>(formula, true)
