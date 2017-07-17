package com.benjishults.bitnots.inference.rules.concrete

import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.inference.rules.NilOpFormula

class PositiveProp(prop: Prop) : NilOpFormula<Prop>(prop, true)
