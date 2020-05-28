package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Tfae

class PositiveTfae(
    override val formula: Tfae
) : BetaFormula<Tfae>, PositiveSignedFormula<Tfae>, AbsractSignedFormula<Tfae>() {
    override fun generateChildren() =
        listOf(
            PositiveAnd(And(*formula.formulas)),
            PositiveAnd(And(*formula.formulas.map { Not(it) }.toTypedArray()))
        )
}
