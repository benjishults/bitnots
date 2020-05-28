package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And

class NegativeAnd(
    override val formula: And
) : BetaFormula<And>, NegativeSignedFormula<And>, AbsractSignedFormula<And>() {
    override fun generateChildren(): List<SignedFormula<Formula>> =
        formula.formulas.map { it.createSignedFormula(false) }
}
