package com.benjishults.bitnots.model.inference.concrete

import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.inference.NilOpFormula

class NegativeProp(prop: Prop) : NilOpFormula<Prop>(prop, false)
