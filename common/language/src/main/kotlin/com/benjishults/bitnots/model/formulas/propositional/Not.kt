package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaWithSubformulas
import com.benjishults.bitnots.model.formulas.PropositionalFormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.NotCompatible
import com.benjishults.bitnots.model.unifier.Substitution

data class Not(val argument: Formula<*>) : FormulaWithSubformulas<PropositionalFormulaConstructor>(PropositionalFormulaConstructor.intern(LogicalOperator.not.name), argument) {
    override fun contains(variable: Variable<*>, sub: Substitution) = argument.contains(variable, sub)

    override fun unifyUncached(other: Formula<*>, sub: Substitution): Substitution =
            if (other is Not)
                Formula.unify(argument, other.argument, sub)
            else
                NotCompatible

    override fun getFreeVariables(): Set<FreeVariable> =
            argument.getFreeVariables()

    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> =
            argument.getVariablesUnboundExcept(boundVars)

    override fun applySub(substitution: Substitution): Not =
            Not(argument.applySub(substitution))

    override fun applyPair(pair: Pair<Variable<*>, Term<*>>): Not =
            Not(argument.applyPair(pair))

    override fun toString() = "(${constructor.name} ${argument})"

}
