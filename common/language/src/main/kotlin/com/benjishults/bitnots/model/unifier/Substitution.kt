package com.benjishults.bitnots.model.unifier

import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.TermConstructor
import com.benjishults.bitnots.model.terms.Variable

/**
 * Idempotent substitution
 */
sealed class Substitution {

    // /**
    //  * The composition of [this] and [other]
    //  */
    // abstract infix fun o(other: Substitution): Substitution
    //
    // /**
    //  * The composition of [this] and [other]
    //  */
    // abstract infix fun o(other: Pair<FreeVariable, Term<*>>): Substitution

    /**
     * The composition of [this] and [other] but only if the two are compatible
     */
    abstract operator fun plus(other: Substitution): Substitution

    /**
     * The composition of [this] and [other] but only if the two are compatible
     */
    abstract operator fun plus(other: Pair<FreeVariable, Term<*>>): Substitution

    abstract operator fun <C : TermConstructor> get(v: Variable<C>): Term<*>

    abstract override fun equals(other: Any?): Boolean

    abstract override fun toString(): String

}

/**
 * The result of an attempted unification of un-unifiable terms.
 */
object NotCompatible : Substitution() {

    override fun plus(other: Pair<FreeVariable, Term<*>>) = this

    override fun <C : TermConstructor> get(v: Variable<C>): Term<*> =
            error("Attempt made to apply a non-existent substitution.")

    // override fun compose(other: Substitution) = this
    // override fun compose(other: Pair<FreeVariable, Term<*>>) = this

    override fun plus(other: Substitution) = this

    override fun equals(other: Any?) = other === this
    override fun toString() = "\u22A5"
}

object EmptySub : Substitution() {

    override fun plus(other: Pair<FreeVariable, Term<*>>) = Sub(other)

    override fun <C : TermConstructor> get(v: Variable<C>): Term<*> = v

    // override fun compose(other: Substitution) = other
    // override fun compose(other: Pair<FreeVariable, Term<*>>) = Sub(other)

    override fun plus(other: Substitution) = other

    override fun equals(other: Any?): Boolean = this === other
    override fun toString(): String = "{}"

}

/*

Identity:

Two substitutions are considered equal if they map each variable to structurally equal result terms,
formally: σ = τ if xσ = xτ for each variable x ∈ V.

For example, { x ↦ 2, y ↦ 3+4 } is equal to { y ↦ 3+4, x ↦ 2 },
but different from { x ↦ 2, y ↦ 7 }.

Composition:

The composition of two substitutions σ = { x1 ↦ t1, ..., xk ↦ tk } and τ = { y1 ↦ u1, ..., yl ↦ ul }
is obtained by removing from the substitution { x1 ↦ t1τ, ..., xk ↦ tkτ, y1 ↦ u1, ..., yl ↦ ul }
those pairs yi ↦ ui for which yi ∈ { x1, ..., xk }.
The composition of σ and τ is denoted by στ.
Composition is an associative operation, and is compatible with substitution application,
i.e. (ρσ)τ = ρ(στ), and (tσ)τ = t(στ), respectively, for every substitutions ρ, σ, τ, and every term t.

Idempotence:

A substitution σ is called idempotent if σσ = σ, and hence tσσ = tσ for every term t.
The substitution { x1 ↦ t1, ..., xk ↦ tk } is idempotent if and only if none of the variables xi occurs in any ti.

The substitution { x ↦ y+y } is idempotent,
e.g. ((x+y) {x↦y+y}) {x↦y+y} = ((y+y)+y) {x↦y+y} = (y+y)+y,
while the substitution { x ↦ x+y } is non-idempotent,
e.g. ((x+y) {x↦x+y}) {x↦x+y} = ((x+y)+y) {x↦x+y} = ((x+y)+y)+y.

Non-commutativity of composition:

Substitution composition is not commutative,
that is, στ may be different from τσ,
even if σ and τ are idempotent.

An example for non-commuting substitutions is { x ↦ y } { y ↦ z } = { x ↦ z, y ↦ z },
but { y ↦ z} { x ↦ y} = { x ↦ y, y ↦ z }.

Question:

Is it enough to apply an order on variables and only allow substitutions in one direction in that ordering?
Would we also need to order terms?

 */

class Sub private constructor(private var map: Map<Variable<*>, Term<*>>) : Substitution() {

    constructor(vararg pairs: Pair<Variable<*>, Term<*>>) : this(mapOf(*pairs))

    init {
        // require idempotence and any variable/term ordering requirements
        map = idempotentVersion()
        // println(this)
        require(isIdempotent())
    }

    private fun isIdempotent() =
            map.values.none { term ->
                map.keys.any { term.contains(it, EmptySub) }
            }

    private fun idempotentVersion(): Map<Variable<*>, Term<*>> {
        val newMap = mutableMapOf<Variable<*>, Term<*>>()
        map.forEach { (myKey, myValue) ->
            map.keys.forEach { myOtherKey ->
                if (myValue.contains(myOtherKey, EmptySub)) {
                    // not idempotent
                    myValue.applySub(this).takeIf { result ->
                        result != myKey
                    }?.let { newValue ->
                        newMap.put(myKey, newValue)
                    }
                }
            }
        }
        return if (newMap.isNotEmpty()) {
            println("WARN: non-idempotent substitution: $this")
            newMap.also { m ->
                map.forEach {
                    m.putIfAbsent(it.key, it.value)
                }
            }.also {
                println("WARN:              replacing with: ${toString(newMap)}")
            }
        } else map
    }

    // override fun compose(other: Pair<FreeVariable, Term<*>>): Substitution =
    //         this + Sub(other)

    override fun <C : TermConstructor> get(v: Variable<C>): Term<*> =
            map[v] ?: v

    // TODO think about what compatibility means
    /**
     * TODO Do we need to ensure the two are commutative?  Or just that no variable in other is among the keys of this?
     */

    fun compose(other: Substitution): Substitution =
            when (other) {
                NotCompatible -> NotCompatible
                EmptySub      -> this
                is Sub        -> {
                    val newMap = mutableMapOf<Variable<*>, Term<*>>()
                    map.entries.map { (v, term) ->
                        // apply arg to value in receiver
                        term.applySub(other).also {
                            if (it !== v)
                                newMap[v] = it
                            else
                                newMap.remove(v)
                        }
                    }
                    other.map.entries.map { (v, term) ->
                        // if arg covers more variables, add them
                        if (v !in newMap.keys) {
                            newMap.put(v, term)
                        }
                    }
                    Sub(newMap)
                }
            }

    override fun plus(other: Substitution): Substitution =
            when (other) {
                NotCompatible -> NotCompatible
                EmptySub      -> this
                is Sub        -> {
                    val conflictResolutions = mutableListOf<Substitution>()
                    // look for conflicts
                    other.map.forEach { (otherKey, otherValue) ->
                        map[otherKey]?.let { myValue ->
                            myValue.unifyUncached(otherValue, EmptySub).takeUnless {
                                it === NotCompatible
                            }?.let { compatibleSub ->
                                conflictResolutions.add(compatibleSub)
                            } ?: return NotCompatible
                        }
                    }
                    val newMap = mutableMapOf<Variable<*>, Term<*>>()
                    map.entries.map { (v, term) ->
                        // apply arg to value in receiver
                        term.applySub(other).also { result ->
                            if (result !== v)
                                newMap[v] = result
                        }
                    }
                    other.map.entries.map { (v, term) ->
                        // if arg covers more variables, add them
                        if (v !in newMap.keys) {
                            // TODO do I need to apply anything to term?  What if it contains variables from [this]?
                            newMap[v] = term
                        }
                    }
                    conflictResolutions.fold(Sub(newMap)) { onGoingSub: Substitution, otherSub ->
                        onGoingSub + otherSub
                    }
                }
            }


    override fun plus(other: Pair<FreeVariable, Term<*>>): Substitution = this.plus(Sub(other))

    override fun hashCode()
            : Int =
            map.entries.fold(0) { o, p ->
                o + p.key.hashCode() + p.value.hashCode()
            }

    override fun equals(other: Any?
    )
            : Boolean {
        // return true if each is more general than the other... i.e. if they are *equivalent*.
        other?.let {
            return (other is Sub &&
                    other.map.size == map.size &&
                    other.map.keys.containsAll(map.keys) &&
                    other.map.entries.all { map.get(it.key) == it.value })
        }
        return false
    }

    override fun toString(): String = toString(map)

    private fun toString(map: Map<Variable<*>, Term<*>>) =
            buildString {
                append("{")
                    .append(map.entries.fold(mutableListOf<String>()) { s, t ->
                        s.also { it.add("${t.key}\u2005\u21A6\u2005${t.value}") }
                    }.joinToString(", "))
                    .append("}")
            }

}
