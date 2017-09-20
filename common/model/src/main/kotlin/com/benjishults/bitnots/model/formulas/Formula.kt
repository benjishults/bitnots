package com.benjishults.bitnots.model.formulas

import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Substitution

abstract class Formula<C : FormulaConstructor>(val constructor: C) {
    abstract fun unify(other: Formula<*>, sub: Substitution = EmptySub): Substitution
    /**
     * 
     */
    abstract fun applySub(substitution: Substitution): Formula<C>

    /**
     * 
     */
    abstract fun applySubDestructive(substitution: Substitution): Formula<C>
    
    abstract fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>>
    abstract fun getFreeVariables(): Set<FreeVariable>

    abstract fun contains(variable: Variable<*>, sub: Substitution): Boolean

    override fun toString() = "(${constructor.name})"
}
