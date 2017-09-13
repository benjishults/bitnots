package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import com.benjishults.bitnots.model.terms.Function.FunctionConstructor
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Sub
import com.benjishults.bitnots.model.unifier.Substitution

abstract class DeltaFormula<F : VarBindingFormula>(formula: F, sign: Boolean) : SignedFormula<F>(formula, sign) {
    override fun generateChildren(): List<SignedFormula<Formula<*>>> {
        val unboundVars = formula.formula.getFreeVariables()
        val skolems = formula.variables.fold(EmptySub) { s: Substitution, t ->
            t.cons.name
            s + Sub(t.to(FunctionConstructor.new(t.cons.name, unboundVars.size)(unboundVars.toList())))
        }
        return listOf(formula.formula.applySub(skolems).createSignedFormula(sign))
    }
}
