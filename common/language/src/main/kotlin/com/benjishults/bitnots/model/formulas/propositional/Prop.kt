package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.PropositionalFormulaConstructor
import com.benjishults.bitnots.util.intern.InternTable

fun Prop(cons : String) = PropositionalVariable.intern(cons)
fun PropU(cons : String) = PropositionalVariable.newSimilar(cons)

class PropositionalVariable private constructor(cons: String) : AtomicPropositionalFormula(
        PropositionalFormulaConstructor.intern(cons)) {

    companion object : InternTable<PropositionalVariable>({ name -> PropositionalVariable(name) })
}
