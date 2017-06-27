package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.util.InternTable

class Function private constructor(name: String, vararg val arguments: Term) : Term(TermConstructor(name)) {
	override fun getFreeVariables(): Set<FreeVariable> =
			arguments.fold(emptySet<FreeVariable>()) { s, t -> s.union(t.getFreeVariables()) }

	override fun substitute(map: Map<Variable, Term>): Function {
		return Function.intern(cons.name, *arguments.map { it.substitute(map) }.toTypedArray())
	}

	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> {
		val value = mutableSetOf<Variable>()
		arguments.map { value.addAll(it.getVariablesUnboundExcept(boundVars)) }
		return value.toSet()
	}

	companion object inner : InternTable<Function, Term>({ name, args -> Function(name, *args) })

	override fun toString() = "(${cons.name} ${arguments.joinToString(" ")})"

	override fun equals(other: Any?): Boolean {
		if (other === null) return false
		if (other::class === this::class) {
			if ((other as Function).arguments.size == this.arguments.size) {
				for (index in 0..this.arguments.size - 1) {
					if (other.arguments[index] != this.arguments[index])
						return false
				}
				return true
			}
		}
		return false
	}
}

fun Fun(name: String, vararg arguments: Term): Function = Function.intern(name, *arguments)
