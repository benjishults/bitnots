package com.benjishults.bitnots.model.unifier

import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.TermConstructor
import com.benjishults.bitnots.model.terms.Variable

sealed class Substitution {

    abstract operator fun plus(other: Substitution): Substitution
    abstract operator fun plus(other: Pair<FreeVariable, Term<*>>): Substitution
    abstract operator fun <C : TermConstructor> get(v: Variable<C>): Term<*>

    abstract override fun equals(other: Any?): Boolean

    abstract override fun toString(): String
}

/**
 * The result of an attempted unification of un-unifiable terms.
 */
object NotUnifiable : Substitution() {
    override fun plus(other: Pair<FreeVariable, Term<*>>): Substitution = this

    override fun <C : TermConstructor> get(v: Variable<C>): Term<*> = error("Attempt made to apply a non-existent substitution.")

    override fun plus(other: Substitution): Substitution = this

    override fun equals(other: Any?): Boolean = other === this
    override fun toString(): String = "\u22A5"
}

object EmptySub : Substitution() {
    override fun plus(other: Pair<FreeVariable, Term<*>>): Substitution = Sub(other)

    override fun <C : TermConstructor> get(v: Variable<C>): Term<*> = v

    override fun plus(other: Substitution) = other

    override fun equals(other: Any?): Boolean = this === other
    override fun toString(): String = "{}"

}

class Sub private constructor(private val map: Map<Variable<*>, Term<*>>) : Substitution() {

    override fun plus(other: Pair<FreeVariable, Term<*>>): Substitution {
        require(other.first !in map)
        return this + Sub(other)
    }

    constructor(vararg pairs: Pair<Variable<*>, Term<*>>) : this(mapOf(*pairs))

    override fun <C : TermConstructor> get(v: Variable<C>): Term<*> {
        return map[v] ?: v
    }

    override fun plus(other: Substitution): Substitution {
        return when (other) {
            NotUnifiable -> NotUnifiable
            EmptySub -> this
            is Sub -> {
                val keys = map.keys.toSet()
                val newMap = mutableMapOf<Variable<*>, Term<*>>()

                map.entries.map { (v, term) ->
                    // apply arg to value in receiver
                    term.applySub(other).let {
                        if (it !== v)
                            newMap[v] = it
                        else
                            newMap -= v
                    }
                }

                other.map.entries.map { (v, term) ->
                    // if arg covers more variables, add them
                    if (v !in keys) {
                        newMap.put(v, term)
                    }
                }

                Sub(newMap)
            }
        }
    }

    override fun equals(other: Any?): Boolean // = this === other
    {
        // return true if each is more general than the other... i.e. if they are *equivalent*.
        other?.let {
            return (other is Sub &&
                    other.map.size == map.size &&
                    other.map.keys.containsAll(map.keys) &&
                    other.map.entries.all { map.get(it.key) == it.value })
        }
        return false
    }

    override fun toString(): String =
            "{" + map.entries.fold(mutableListOf<String>())
            {
                s, t ->
                s.also { it.add("${t.key}\u2005\u21A6\u2005${t.value}") }
            }.joinToString(", ") + "}"

}
