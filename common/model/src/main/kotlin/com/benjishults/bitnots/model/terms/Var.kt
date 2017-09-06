package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.terms.BoundVariable.BVConstructor
import com.benjishults.bitnots.model.terms.FreeVariable.FVConstructor
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Sub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.util.InternTable

sealed class Variable<C : TermConstructor>(name: C) : Term<C>(name) {

    override fun applySub(substitution: Substitution): Term<*> =
            substitution[this]

    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> =
            if (this in boundVars)
                setOf()
            else
                setOf(this)

    override fun toString(): String = cons.name
}

class BoundVariable private constructor(name: String) : Variable<BVConstructor>(BVConstructor(name)) {
    override fun containsInternal(variable: Variable<*>, sub: Substitution): Boolean {
        TODO()
    }

    class BVConstructor(name: String) : TermConstructor(name)

    override fun unify(other: Term<*>, sub: Substitution): Substitution =
            if (other === this)
                sub
            else
                NotUnifiable

    override fun getFreeVariables(): Set<FreeVariable> = emptySet()
//    override fun getFreeVariablesAndCounts(): MutableMap<FreeVariable, Int> = mutableMapOf()

    companion object : InternTable<BoundVariable>({ name -> BoundVariable(name) })

}

fun BV(name: String): BoundVariable = BoundVariable.intern(name)

class FreeVariable private constructor(name: String) : Variable<FVConstructor>(FVConstructor(name)) {

    /**
     * @param variable must not be bound by [sub]
     */
    override fun containsInternal(variable: Variable<*>, sub: Substitution): Boolean =
            if (this === variable)
                true
            else {
                sub[this].let {
                    it !== this && it.containsInternal(variable, sub)
                }
            }

    class FVConstructor(name: String) : TermConstructor(name)

    override fun unify(other: Term<*>, sub: Substitution): Substitution {
        sub[this].let {
            if (it !== this)
                return it.unify(other, sub)
        }
        // INVARIANT: this is not bound by sub
        return if (this === other)
            sub
        else if (other.contains(this, sub))
            NotUnifiable
        else if (other is FreeVariable)
            sub[other].let {
                if (it !== other)
                    it.unify(this, sub)
                else
                    sub + (this to other.applySub(sub))
            }
        else
            sub + (this to other.applySub(sub))
    }

    override fun getFreeVariables(): Set<FreeVariable> = setOf(this)
//    override fun getFreeVariablesAndCounts(): MutableMap<FreeVariable, Int> = mutableMapOf(this.to(1))

    companion object : InternTable<FreeVariable>({ name -> FreeVariable(name) })

}

fun FV(name: String): FreeVariable = FreeVariable.intern(name)
