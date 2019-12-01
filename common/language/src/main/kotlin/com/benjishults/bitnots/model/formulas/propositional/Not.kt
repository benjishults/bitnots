package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Substitution

data class Not(val argument: Formula<*>) : PropositionalFormula(FormulaConstructor.intern(LogicalOperators.not.name)) {
    override fun contains(variable: Variable<*>, sub: Substitution) = argument.contains(variable, sub)

    override fun unifyUncached(other: Formula<*>, sub: Substitution): Substitution =
            if (other is Not)
                Formula.unify(argument, other.argument, sub)
            else
                NotUnifiable

    override fun getFreeVariables(): Set<FreeVariable> =
            argument.getFreeVariables()

    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> =
            argument.getVariablesUnboundExcept(boundVars)

    override fun applySub(substitution: Substitution): Not =
            Not(argument.applySub(substitution))

    override fun toString() = "(${constructor.name} ${argument})"

}
