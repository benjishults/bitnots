package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.SignedFormulaFactory
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.theory.Theory

data class PositiveAnd(
    override val formula: And
) : AlphaFormula<And>, PositiveSignedFormula<And>, AbsractSignedFormula<And>() {
    override fun generateChildren(theory: Theory): List<SignedFormula<Formula>> =
        formula.formulas.map { SignedFormulaFactory.createSignedFormula(it, true) }
}
