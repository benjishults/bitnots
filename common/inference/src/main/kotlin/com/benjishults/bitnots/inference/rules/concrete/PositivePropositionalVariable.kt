package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.AbsractSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.rules.NilOpFormula
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable

data class PositivePropositionalVariable(
    override val formula: PropositionalVariable
) : NilOpFormula<PropositionalVariable>,
    PositiveSignedFormula<PropositionalVariable>,
    AbsractSignedFormula<PropositionalVariable>()
