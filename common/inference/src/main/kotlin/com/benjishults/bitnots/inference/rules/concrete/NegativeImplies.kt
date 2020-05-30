package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.SignedFormulaFactory
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.theory.Theory

data class NegativeImplies(
    override val formula: Implies
) : AlphaFormula<Implies>, NegativeSignedFormula<Implies>, AbsractSignedFormula<Implies>() {
    override fun generateChildren(theory: Theory): List<SignedFormula<Formula>> =
        listOf(SignedFormulaFactory.createSignedFormula(formula.antecedent, true), SignedFormulaFactory.createSignedFormula(formula.consequent, false))
}
