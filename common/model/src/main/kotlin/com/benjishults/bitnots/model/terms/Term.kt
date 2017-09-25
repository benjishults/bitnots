package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.util.memoize
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Substitution

abstract class Term<C : TermConstructor>(val cons: C) {

    companion object {
        /**
         * Returns the substitution
         */
        val unify = object : (Term<*>, Term<*>, Substitution) -> Substitution {
            override fun invoke(first: Term<*>, second: Term<*>, sub: Substitution): Substitution {
                return first.unifyUnchached(second, sub)
            }
        }.memoize()
    }

    /**
     * The behavior of this function is undefined if [sub] is not idempotent or if variables in [sub] occur in either receiver or [other].
     * This returns an idempotent most general unifier of the receiver and [other] or [NotUnifiable] if there is no unifier.
     */
    protected abstract fun unifyUnchached(other: Term<*>, sub: Substitution = EmptySub): Substitution

    abstract fun applySub(substitution: Substitution): Term<*>
    /**
     * @param variable must not be bound by [sub]
     */
    fun contains(variable: Variable<*>, sub: Substitution): Boolean {
        require(variable.applySub(sub) === variable) {
            "When calling Term::contains, the variable argument (was $variable) must not occur in the substitution (was $sub)."
        }
        return containsInternal(variable, sub)
    }

    internal abstract fun containsInternal(variable: Variable<*>, sub: Substitution): Boolean

    abstract fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>>
    abstract fun getFreeVariables(): Set<FreeVariable>
    /**
     * maps free variables occurring in the receiver to the number of times the variable occurs in the receiver
     */
    //    abstract fun getFreeVariablesAndCounts(): MutableMap<FreeVariable, Int>
    override abstract fun equals(other: Any?): Boolean
    override abstract fun hashCode(): Int
}
