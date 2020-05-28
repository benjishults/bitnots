package com.benjishults.bitnots.inference.rules

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.VarsBindingFormula
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Sub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.util.Counter

interface GammaFormula<F : VarsBindingFormula> : SignedFormula<F>, Counter {

    override fun generateChildren(): List<SignedFormula<Formula>> {
        val boundToFree = formula.variables.fold(EmptySub) { s: Substitution, t ->
            s + Sub(
                t to if (FreeVariable.exists(t.cons.name))
                    FreeVariable.newSimilar(t.cons.name)
                else
                    FV(t.cons.name)
            )
        }
        return listOf(formula.formula.applySub(boundToFree).createSignedFormula(sign))
    }

}
