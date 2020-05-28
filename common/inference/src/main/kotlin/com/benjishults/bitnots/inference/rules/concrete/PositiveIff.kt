package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Not

class PositiveIff(
    override val formula: Iff
) : BetaFormula<Iff>, PositiveSignedFormula<Iff>, AbsractSignedFormula<Iff>() {
    override fun generateChildren() =
        listOf(
            PositiveAnd(And(formula.first, formula.second)),
            PositiveAnd(And(Not(formula.first), Not(formula.second)))
        )
}
