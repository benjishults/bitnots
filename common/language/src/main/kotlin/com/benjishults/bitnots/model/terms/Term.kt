package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotCompatible
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.util.memoize

abstract class Term<C : TermConstructor>(val cons: C) {

    companion object {
        /**
         * Returns the substitution
         */
        val unify = object : (Term<*>, Term<*>, Substitution) -> Substitution {
            override fun invoke(first: Term<*>, second: Term<*>, sub: Substitution): Substitution =
                    if (sub === NotCompatible)
                        sub
                    else
                        first.unifyUncached(second, sub)
        }.memoize()
    }

    /**
     * TODO must verify that the substitution always has been applied to each side or that its application would be a no-op.
     * The behavior of this function is undefined if [sub] is not idempotent or if variables in [sub] occur in either receiver or [other].
     * This returns an idempotent most general unifier of the receiver and [other] or [NotCompatible] if there is no unifier.
     */
    abstract fun unifyUncached(other: Term<*>, sub: Substitution = EmptySub): Substitution

    /**
     * Application of a substitution is defined as the simultaneous replacement of all variables.
     * If the substitution is idempotent, then this distinction doesn't matter.
     */
    abstract fun applySub(substitution: Substitution): Term<*>

    /**
     * Application of a substitution is defined as the simultaneous replacement of all variables.
     * If the substitution is idempotent, then this distinction doesn't matter.
     */
    abstract fun applyPair(pair: Pair<Variable<*>, Term<*>>): Term<*>

    /**
     * @param variable must not be bound by [sub]
     */
    fun contains(variable: Variable<*>, sub: Substitution = EmptySub): Boolean {
        require(variable.applySub(sub) === variable) {
            "When calling Term::contains, the variable argument (was $variable) must not occur in the substitution (was $sub)."
        }
        return containsInternal(variable, sub)
    }

    internal abstract fun containsInternal(variable: Variable<*>, sub: Substitution): Boolean

    abstract fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>>
    abstract fun getFreeVariables(): Set<FreeVariable>
    //    /**
    //     * maps free variables occurring in the receiver to the number of times the variable occurs in the receiver
    //     */
    //    abstract fun getFreeVariablesAndCounts(): MutableMap<FreeVariable, Int>
    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int
}
