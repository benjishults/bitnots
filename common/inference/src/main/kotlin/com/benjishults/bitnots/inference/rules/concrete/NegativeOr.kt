package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.SignedFormulaFactory
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.theory.Theory

data class NegativeOr(
    override val formula: Or
) : AlphaFormula<Or>, NegativeSignedFormula<Or>, AbsractSignedFormula<Or>() {
    override fun generateChildren(theory: Theory): List<SignedFormula<Formula>> = formula.formulas.map { SignedFormulaFactory.createSignedFormula(it, false) }
}
