package com.benjishults.bitnots.model.formulas.fol

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.Substitution

abstract class VarBindingFormula(cons: FormulaConstructor, val formula: Formula, vararg val variables: BoundVariable) : Formula(cons) {
    override fun unify(other: Formula, sub: Substitution): Substitution {
        TODO()
    }

    override fun getFreeVariables(): Set<FreeVariable> = formula.getFreeVariables()

    override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> {
        return formula.getVariablesUnboundExcept(boundVars.plus(variables))
    }

    override fun applySub(substitution: Substitution): Formula {
        return this::class.constructors.first().call(formula.applySub(substitution), variables)
    }

    override fun toString(): String = "(${constructor.name} (${variables.joinToString(" ")}) ${formula})"
}