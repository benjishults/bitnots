package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And

class NegativeAnd(and: And) :
        BetaFormula<And>(and, false) {
    override fun generateChildren(): List<SignedFormula<Formula<*>>> = formula.formulas.map { it.createSignedFormula(false) }
}
