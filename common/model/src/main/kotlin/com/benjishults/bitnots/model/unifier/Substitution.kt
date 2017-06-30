package com.benjishults.bitnots.model.unifier

import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable

sealed class Substitution {

	abstract fun compose(other: Substitution): Substitution
	abstract fun compose(pair: Pair<Variable, Term>): Substitution

	abstract override fun equals(other: Any?): Boolean
}

/**
 * The result of an attempted unification of un-unifiable terms.
 */
object NotUnifiable : Substitution() {

	override fun compose(other: Substitution): Substitution = this
	override fun compose(pair: Pair<Variable, Term>): Substitution = this

	override fun equals(other: Any?): Boolean = other === this
}

object EmptySub : Substitution() {

	override fun compose(other: Substitution) = other
	override fun compose(pair: Pair<Variable, Term>) = Sub(pair)

	override fun equals(other: Any?): Boolean = this === other
}

class Sub(val map: Map<Variable, Term>) : Substitution() {
	constructor(vararg pairs: Pair<Variable, Term>) : this(mapOf(*pairs))

	override fun compose(other: Substitution): Substitution {
		return other
	}

	override fun compose(pair: Pair<Variable, Term>): Substitution {
		return other
	}

	override fun equals(other: Any?): Boolean {
		// return true if each is more general than the other... i.e. if they are *equivalent*.
		other?.let {
			if (other::class === this::class) {
				if ((other as Substitution).map.size == this.arguments.size) {

					return false
				}
			}
		}
		return false
	}
}