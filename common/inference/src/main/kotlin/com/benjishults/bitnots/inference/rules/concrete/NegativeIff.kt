package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Implies


class NegativeIff(
    override val formula: Iff
) : BetaFormula<Iff>, NegativeSignedFormula<Iff>, AbsractSignedFormula<Iff>() {
    override fun generateChildren(): List<SignedFormula<Formula>> =
        listOf(
            NegativeImplies(Implies(formula.first, formula.second)),
            NegativeImplies(Implies(formula.second, formula.first))
        )

}
