package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.util.InternTable

class Prop private constructor(cons: String) : AtomicPropositionalFormula(FormulaConstructor.intern(cons)) {
	companion object inner : InternTable<Prop>({ name -> Prop(name) })
}
