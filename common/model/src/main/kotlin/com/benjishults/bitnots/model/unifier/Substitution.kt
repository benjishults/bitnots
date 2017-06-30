package com.benjishults.bitnots.model.unifier

import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable

sealed class Substitution {

    abstract fun compose(other: Substitution): Substitution
    abstract fun compose(pair: Pair<Variable, Term>): Substitution
    abstract fun applyTo(v: Variable): Term

    abstract override fun equals(other: Any?): Boolean
}

/**
 * The result of an attempted unification of un-unifiable terms.
 */
object NotUnifiable : Substitution() {
    override fun applyTo(v: Variable) = throw IllegalStateException("Attempt made to apply a non-existent substitution.")


    override fun compose(other: Substitution): Substitution = this
    override fun compose(pair: Pair<Variable, Term>): Substitution = this

    override fun equals(other: Any?): Boolean = other === this
}

object EmptySub : Substitution() {
    override fun applyTo(v: Variable) = v;

    override fun compose(other: Substitution) = other
    override fun compose(pair: Pair<Variable, Term>) = Sub(pair)

    override fun equals(other: Any?): Boolean = this === other
}

class Sub(val map: Map<Variable, Term>) : Substitution() {
    override fun applyTo(v: Variable): Term {
        map.entries.forEach { if (v === it.key) return it.value }
        return v
    }

    constructor(vararg pairs: Pair<Variable, Term>) : this(mapOf(*pairs))

    override fun compose(other: Substitution): Substitution {
        return other
    }

    override fun compose(pair: Pair<Variable, Term>): Substitution {
        TODO()
    }

    override fun equals(other: Any?): Boolean {
        TODO()
        // return true if each is more general than the other... i.e. if they are *equivalent*.
//		other?.let {
//			if (other::class === this::class) {
//				if ((other as Substitution).map.size == this.arguments.size) {
//
//					return false
//				}
//			}
//		}
//		return false
    }
}