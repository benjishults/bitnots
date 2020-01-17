package com.benjishults.bitnots.model.formulas

import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.util.memoize

// NOTE should be immutable
abstract class Formula<C : FormulaConstructor>(val constructor: C) {
    companion object {
        /**
         * Returns the substitution
         */
        val unify = object : (Formula<*>, Formula<*>, Substitution) -> Substitution {
            override fun invoke(first: Formula<*>, second: Formula<*>, sub: Substitution): Substitution {
                return first.unifyUncached(second, sub)
            }
        }.memoize()
    }

    protected abstract fun unifyUncached(other: Formula<*>, sub: Substitution = EmptySub): Substitution

    abstract fun applySub(substitution: Substitution): Formula<C>
    abstract fun applyPair(pair: Pair<Variable<*>, Term<*>>): Formula<C>

    abstract fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>>
    abstract fun getFreeVariables(): Set<FreeVariable>

    abstract fun contains(variable: Variable<*>, sub: Substitution): Boolean

    override fun toString() = "(${constructor.name})"
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
}

abstract class FormulaWithSubformulas<C: FormulaConstructor>(constructor: C, vararg val formulas: Formula<*>): Formula<C>(constructor)
