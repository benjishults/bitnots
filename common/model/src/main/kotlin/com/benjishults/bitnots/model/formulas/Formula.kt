package com.benjishults.bitnots.model.formulas

import com.benjishults.bitnots.model.util.memoize
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Substitution

abstract class Formula<C : FormulaConstructor>(val constructor: C) {
    companion object {
        /**
         * Returns the substitution
         */
        val unify = object : (Formula<*>, Formula<*>, Substitution) -> Substitution {
            override fun invoke(first: Formula<*>, second: Formula<*>, sub: Substitution): Substitution {
                return first.unifyUnchached(second, sub)
            }
        }.memoize()
    }

    protected abstract fun unifyUnchached(other: Formula<*>, sub: Substitution = EmptySub): Substitution

    abstract fun applySub(substitution: Substitution): Formula<C>

    abstract fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>>
    abstract fun getFreeVariables(): Set<FreeVariable>

    abstract fun contains(variable: Variable<*>, sub: Substitution): Boolean

    override fun toString() = "(${constructor.name})"
    override abstract fun equals(other: Any?): Boolean
    override abstract fun hashCode(): Int
}
