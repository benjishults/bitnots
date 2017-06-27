package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.inference.NilOpFormula

class PositiveProp(prop: Prop) : NilOpFormula<Prop>(prop, true)
