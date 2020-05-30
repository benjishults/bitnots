package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Tfae
import com.benjishults.bitnots.theory.Theory

data class PositiveTfae(
    override val formula: Tfae
) : BetaFormula<Tfae>, PositiveSignedFormula<Tfae>, AbsractSignedFormula<Tfae>() {
    override fun generateChildren(theory: Theory): List<SignedFormula<Formula>> =
        listOf(
            PositiveAnd(And(*formula.formulas)),
            PositiveAnd(And(*formula.formulas.map { Not(it) }.toTypedArray()))
        )
}
