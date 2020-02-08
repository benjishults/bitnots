package com.benjishults.bitnots.model.formulas

import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.util.memo.memoize

// NOTE should be immutable
interface Formula<C : FormulaConstructor> {
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

    val constructor: C
    fun unifyUncached(other: Formula<*>, sub: Substitution = EmptySub): Substitution

    fun applySub(substitution: Substitution): Formula<C>
    fun applyPair(pair: Pair<Variable<*>, Term<*>>): Formula<C>

    fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>>
    fun getFreeVariables(): Set<FreeVariable>

    fun contains(variable: Variable<*>, sub: Substitution): Boolean

    // override fun toString() = "(${constructor.name})"
    // override fun equals(other: Any?): Boolean
    // override fun hashCode(): Int
}

abstract class FormulaWithSubformulas<C : FormulaConstructor>(
        override val constructor: C,
        vararg val formulas: Formula<*>
) : Formula<C> {
    override fun toString(): String = buildString {
        append("(${constructor.name}")
        formulas.forEach { append(" ").append(it) }
        append(")")
    }
}
