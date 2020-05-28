package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.propositional.Implies

class PositiveImplies(
    override val formula: Implies
) : BetaFormula<Implies>, PositiveSignedFormula<Implies>, AbsractSignedFormula<Implies>() {
    override fun generateChildren() =
        listOf(formula.antecedent.createSignedFormula(false), formula.consequent.createSignedFormula(true))
}
