package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.rules.NilOpFormula
import com.benjishults.bitnots.model.formulas.fol.Predicate

data class NegativePredicate(
    override val formula: Predicate
) : NilOpFormula<Predicate>, NegativeSignedFormula<Predicate>, AbsractSignedFormula<Predicate>()
