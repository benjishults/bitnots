package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.util.InternTable

fun Prop(cons : String) = PropositionalVariable.intern(cons)

class PropositionalVariable private constructor(cons: String) : AtomicPropositionalFormula(FormulaConstructor.intern(cons)) {

    companion object : InternTable<PropositionalVariable>({ name -> PropositionalVariable(name) })
}
