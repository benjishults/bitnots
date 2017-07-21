package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.inference.rules.NilOpFormula

class PositiveProp(prop: PropositionalVariable) : NilOpFormula<PropositionalVariable>(prop, true)
