package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.rules.NilOpFormula
import com.benjishults.bitnots.model.formulas.propositional.Falsity

object NegativeFalsity : NilOpFormula<Falsity>, NegativeSignedFormula<Falsity>, AbsractSignedFormula<Falsity>() {
    override val formula = Falsity
}
