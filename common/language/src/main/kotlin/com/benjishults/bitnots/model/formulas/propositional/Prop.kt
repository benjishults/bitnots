package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.util.intern.InternTable

fun Prop(cons : String) = PropositionalVariable.intern(cons)
fun PropU(cons : String) = PropositionalVariable.new(cons)

class PropositionalVariable private constructor(cons: String) : AtomicPropositionalFormula(FormulaConstructor.intern(cons)) {

    companion object : InternTable<PropositionalVariable>({ name -> PropositionalVariable(name) })
}
