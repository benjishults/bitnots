package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.rules.NilOpFormula
import com.benjishults.bitnots.model.formulas.fol.Predicate

class PositivePredicate(
    override val formula: Predicate
) : NilOpFormula<Predicate>, PositiveSignedFormula<Predicate>, AbsractSignedFormula<Predicate>()
