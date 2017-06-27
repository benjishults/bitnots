package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.util.InternTable

abstract class Variable constructor(name: String) : Term(TermConstructor(name)) {
	override fun substitute(map: Map<Variable, Term>): Term = map.get(this) ?: this

	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> = if (this in boundVars) setOf() else setOf(this)

	override fun toString(): String {
		return cons.name
	}
}
