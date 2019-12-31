package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.inference.rules.NilOpFormula
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable

class PositivePropositionalVariable(prop: PropositionalVariable) : NilOpFormula<PropositionalVariable>(prop, true)
