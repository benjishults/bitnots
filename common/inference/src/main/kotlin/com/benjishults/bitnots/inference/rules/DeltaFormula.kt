package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.SignedFormulaFactory
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.VarsBindingFormula
import com.benjishults.bitnots.model.terms.Function.FunctionConstructor
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Sub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.theory.Theory

interface DeltaFormula<F : VarsBindingFormula> : SignedFormula<F> {

    override fun generateChildren(theory: Theory): List<SignedFormula<Formula>> {
        val unboundVars = formula.formula.getFreeVariables()
        val skolems = formula.variables.fold(EmptySub) { s: Substitution, t ->
            t.cons.name
            s + Sub(t to FunctionConstructor.newSimilar(t.cons.name, unboundVars.size)(unboundVars.toList()))
        }
        return listOf(SignedFormulaFactory.createSignedFormula(formula.formula.applySub(skolems), sign))
    }
}
