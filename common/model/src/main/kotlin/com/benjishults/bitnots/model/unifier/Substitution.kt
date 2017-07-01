package com.benjishults.bitnots.model.unifier

import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable

sealed class Substitution {

	abstract fun compose(other: Substitution): Substitution
	abstract fun applyToVar(v: Variable): Term

	abstract override fun equals(other: Any?): Boolean

	abstract override fun toString(): String
}

/**
 * The result of an attempted unification of un-unifiable terms.
 */
object NotUnifiable : Substitution() {

	override fun applyToVar(v: Variable) = error("Attempt made to apply a non-existent substitution.")

	override fun compose(other: Substitution): Substitution = this

	override fun equals(other: Any?): Boolean = other === this
	override fun toString(): String = "\u22A5"

}

object EmptySub : Substitution() {

	override fun applyToVar(v: Variable) = v;

	override fun compose(other: Substitution) = other

	override fun equals(other: Any?): Boolean = this === other
	override fun toString(): String = "{}"

}

class Sub(val map: Map<Variable, Term>) : Substitution() {

	constructor(vararg pairs: Pair<Variable, Term>) : this(mapOf(*pairs))

	override fun applyToVar(v: Variable): Term {
		map.get(v)?.let { return it }
		return v
	}

	override fun compose(other: Substitution): Substitution {
		return when (other) {
			NotUnifiable -> {
				NotUnifiable
			}
			EmptySub -> {
				this
			}
			is Sub -> {
				val doneMap = other.map.entries.fold(map.entries.fold(map) { s, t ->
					// apply arg to value in receiver
					val applied = t.value.applySub(other)
					if (applied !== t.value) {
						s.plus(t.key.to(applied))
					} else {
						s
					}
				}) { s, t ->
					// if arg covers more variables, add them
					if (!map.keys.contains(t.key)) {
						s.plus(t.key.to(t.value))
					} else {
						s
					}
				}
				if (doneMap === map)
					this
				else
					Sub(doneMap)
			}
		}
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

	override fun toString(): String =
			"{" + map.entries.fold(mutableListOf<String>())
			{
				s, t ->
				s.also { it.add("${t.value}/${t.key}") }
			}.joinToString(", ") + "}"

}
