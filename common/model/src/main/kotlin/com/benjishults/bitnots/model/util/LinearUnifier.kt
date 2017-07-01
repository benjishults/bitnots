package com.benjishults.bitnots.model.util

import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.model.terms.Fun
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Substitution

fun linearUnifier(t1: Term, t2: Term): Substitution {

	return NotUnifiable
}

data class MultiEquation(val variables: Set<FreeVariable>, val terms: List<Term>)

private fun makeMultiEqn(vararg terms: Term): MultiEquation {
	val variables = mutableSetOf<FreeVariable>()
	val terms = mutableListOf<Term>()
	terms.forEach {
		variables.addAll(it.getFreeVariables())
		if (it !is FreeVariable) terms.add(it)
	}
	return MultiEquation(variables, terms)
}

private fun DEC(vararg M: Term): Pair<Term, Set<MultiEquation>>? {
	val first = M.first()
	if (M.any { it is BoundVariable } && M.any { it !== first })
		return null
	M.find { it is FreeVariable }?.let { return it.to(setOf(makeMultiEqn(*M))) }
	if (M.all { it.cons == first.cons }) {
		if (first is BoundVariable || (first is Function && first.arguments.isEmpty())) {
			return first.to(emptySet())
		} else {
			val DECs = mutableListOf<Pair<Term, Set<MultiEquation>>>()
			for (i in 0..(first as Function).arguments.size) {
				DECs.add(DEC(*(M as Array<Function>).map { it.arguments[i] }.toTypedArray()) ?: return null)
			}
			return Fun(first.cons.name, *DECs.map { it.first }.toTypedArray()).to(DECs.fold(emptySet()) { s, t -> s.union(t.second) })
		}
	} else
		return null
}
