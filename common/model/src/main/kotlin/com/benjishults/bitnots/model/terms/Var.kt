package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.unifier.Substitution

abstract class Variable constructor(name: String) : Term(TermConstructor(name)) {
	override fun applySub(substitution: Substitution): Term = substitution.applyTo(this)

	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> = if (this in boundVars) setOf() else setOf(this)

	override fun toString(): String {
		return cons.name
	}
}
