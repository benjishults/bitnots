package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.unifier.NotCompatible
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.unifier.get
import com.benjishults.bitnots.util.intern.InternTable

sealed class Variable(name: TermConstructor) : Term(name) {

    override fun applySub(substitution: Substitution): Term =
            substitution[this]

    override fun applyPair(pair: Pair<Variable, Term>): Term =
            pair[this]

    override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> =
            if (this in boundVars)
                setOf()
            else
                setOf(this)

    override fun toString(): String = cons.name

    override fun hashCode(): Int = cons.name.hashCode()

    override fun equals(other: Any?): Boolean =
            other !== null && other::class === this::class &&
            cons.name.equals((other as Variable).cons.name)

}

class BoundVariable private constructor(name: String) : Variable(BVConstructor(name)) {

    override fun containsInternal(variable: Variable, sub: Substitution): Boolean {
        TODO()
    }

    class BVConstructor(name: String) : TermConstructor(name)

    override fun unifyUncached(other: Term, sub: Substitution): Substitution =
            if (other === this)
                sub
            else
                NotCompatible

    override fun getFreeVariables(): Set<FreeVariable> = emptySet()
    //    override fun getFreeVariablesAndCounts(): MutableMap<FreeVariable, Int> = mutableMapOf()

    companion object : InternTable<BoundVariable>({ name -> BoundVariable(name) })

}

fun BV(name: String): BoundVariable = BoundVariable.intern(name)
fun BVU(name: String): BoundVariable = BoundVariable.newSimilar(name)

class FreeVariable private constructor(name: String) : Variable(FVConstructor(name)) {

    /**
     * @param variable must not be bound by [sub]
     */
    override fun containsInternal(variable: Variable, sub: Substitution): Boolean =
            if (this === variable)
                true
            else {
                sub[this].let {
                    it !== this && it.containsInternal(variable, sub)
                }
            }

    class FVConstructor(name: String) : TermConstructor(name)

    override fun unifyUncached(other: Term, sub: Substitution): Substitution {
        if (sub === NotCompatible)
            return sub
        else {
            sub[this].let {
                if (it != this)
                    return Term.unify(it, other, sub)
            }
            // INVARIANT: this is not bound by sub
            return if (this == other)
                sub
            else if (other.contains(this, sub))
                NotCompatible
            else if (other is FreeVariable)
                sub[other].let {
                    if (it != other)
                        Term.unify(it, this, sub)
                    else
                        sub + (this to other.applySub(sub))
                }
            else
                sub + (this to other.applySub(sub))
        }
    }

    override fun getFreeVariables(): Set<FreeVariable> = setOf(this)
    //    override fun getFreeVariablesAndCounts(): MutableMap<FreeVariable, Int> = mutableMapOf(this.to(1))

    companion object : InternTable<FreeVariable>({ name -> FreeVariable(name) })

}

fun FV(name: String): FreeVariable = FreeVariable.intern(name)
fun FVU(name: String): FreeVariable = FreeVariable.newSimilar(name)
