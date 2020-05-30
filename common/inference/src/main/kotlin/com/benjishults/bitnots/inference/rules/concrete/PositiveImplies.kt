package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.SignedFormulaFactory
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.theory.Theory

data class PositiveImplies(
    override val formula: Implies
) : BetaFormula<Implies>, PositiveSignedFormula<Implies>, AbsractSignedFormula<Implies>() {
    override fun generateChildren(theory: Theory): List<SignedFormula<Formula>> =
        listOf(SignedFormulaFactory.createSignedFormula(formula.antecedent, false), SignedFormulaFactory.createSignedFormula(formula.consequent, true))
}
