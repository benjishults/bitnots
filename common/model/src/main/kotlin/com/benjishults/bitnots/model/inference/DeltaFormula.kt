package com.benjishults.bitnots.model.inference

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.unifier.EmptySub

abstract class DeltaFormula<F : VarBindingFormula>(formula: F, sign: Boolean) : SignedFormula<F>(formula, sign) {
    override fun generateChildren(): List<SignedFormula<out Formula>> {
        val unboundVars = formula.formula.getFreeVariables()
        val skolems = formula.variables.fold(EmptySub) { s: Substitution, t -> s.compose(t.to(Function.new(t.cons.name, *unboundVars.toTypedArray()))) }
        return listOf(formula.formula.applySub(skolems).createSignedFormula(sign))
    }
}
