package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Substitution

abstract class Term<C : TermConstructor>(val cons: C) {
    /**
     * The behavior of this function is undefined if [sub] is not idempotent or if [sub] has not been applied to both the receiver and [other].
     * This returns an idempotent most general unifier of the receiver and [other] or [NotUnifiable] if there is no unifier.
     */
    abstract fun unify(other: Term<*>, sub: Substitution = EmptySub): Substitution

    abstract fun applySub(substitution: Substitution): Term<*>
    //	abstract operator fun contains(variable: Variable<*>): Boolean
    abstract fun contains(variable: Variable<*>, sub: Substitution): Boolean

    abstract fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>>
    abstract fun getFreeVariables(): Set<FreeVariable>
    /**
     * maps free variables occurring in the receiver to the number of times the variable occurs in the receiver
     */
//    abstract fun getFreeVariablesAndCounts(): MutableMap<FreeVariable, Int>
}
