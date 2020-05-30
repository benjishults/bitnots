package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Tfae
import com.benjishults.bitnots.theory.Theory

data class NegativeTfae(
    override val formula: Tfae
) : AlphaFormula<Tfae>, NegativeSignedFormula<Tfae>, AbsractSignedFormula<Tfae>() {
    override fun generateChildren(theory: Theory): List<SignedFormula<Formula>> =
        listOf(
            NegativeAnd(And(*formula.formulas)),
            NegativeAnd(And(*formula.formulas.map { Not(it) }.toTypedArray()))
        )
}
