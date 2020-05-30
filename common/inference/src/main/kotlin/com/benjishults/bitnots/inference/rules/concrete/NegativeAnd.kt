package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.SignedFormulaFactory
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.theory.Theory

data class NegativeAnd(
    override val formula: And
) : BetaFormula<And>, NegativeSignedFormula<And>, AbsractSignedFormula<And>() {

    override fun generateChildren(theory: Theory): List<SignedFormula<Formula>> =
        formula.formulas.map { SignedFormulaFactory.createSignedFormula(it, false) }

}
