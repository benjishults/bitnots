package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Implies

class NegativeImplies(
    override val formula: Implies
) : AlphaFormula<Implies>, NegativeSignedFormula<Implies>, AbsractSignedFormula<Implies>() {
    override fun generateChildren(): List<SignedFormula<Formula>> =
        listOf(formula.antecedent.createSignedFormula(true), formula.consequent.createSignedFormula(false))
}
