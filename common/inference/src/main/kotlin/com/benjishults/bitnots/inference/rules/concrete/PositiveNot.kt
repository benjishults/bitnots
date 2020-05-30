package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.SignedFormulaFactory
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.theory.Theory

data class PositiveNot(
    override val formula: Not
) : AlphaFormula<Not>, PositiveSignedFormula<Not>, AbsractSignedFormula<Not>() {
    override fun generateChildren(theory: Theory): List<SignedFormula<Formula>> =
        listOf(SignedFormulaFactory.createSignedFormula(formula.argument, false))
}
