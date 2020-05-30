package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.rules.NilOpFormula
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable

data class NegativePropositionalVariable(
    override val formula: PropositionalVariable
) : NilOpFormula<PropositionalVariable>,
    NegativeSignedFormula<PropositionalVariable>,
    AbsractSignedFormula<PropositionalVariable>()
