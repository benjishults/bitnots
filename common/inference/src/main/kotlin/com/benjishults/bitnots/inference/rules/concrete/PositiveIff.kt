package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.theory.Theory

data class PositiveIff(
    override val formula: Iff
) : BetaFormula<Iff>, PositiveSignedFormula<Iff>, AbsractSignedFormula<Iff>() {
    override fun generateChildren(theory: Theory): List<SignedFormula<Formula>> =
        listOf(
            PositiveAnd(And(formula.first, formula.second)),
            PositiveAnd(And(Not(formula.first), Not(formula.second)))
        )
}
