package com.benjishults.bitnots.model.util

import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Fun
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Substitution

fun linearUnify(t1: Term, t2: Term): Substitution {
	SystemOfMultiEquations(mutableSetOf(MultiEquation(t1, t2)))
	return NotUnifiable
}

data class SystemOfMultiEquations private constructor(val U: MutableSet<MultiEquation>, val T: MutableList<MultiEquation> = mutableListOf()) {

	constructor(U: MutableSet<MultiEquation>) : this(U, mutableListOf())

	fun solve(): Substitution {
		while (true) {
			U.asSequence().filter { it.M.isNotEmpty() }.firstOrNull {
				multiEq ->
				DEC(multiEq.M.first())?.let { (C, F) ->
					if (multiEq.S.intersect(F.map { it.S }).isEmpty())
						return NotUnifiable
					else {
						U.remove(multiEq)
						U.add(MultiEquation(multiEq.S, mutableListOf(C)))
						U.addAll(F)
						U.compactify()
					}
				}
				return NotUnifiable
			} ?: return NotUnifiable
		}
	}

}

fun MutableSet<MultiEquation>.reduce(): Boolean {
	this.asSequence().filter { it.M.isNotEmpty() }.firstOrNull()?.let {
		multiEq ->
		DEC(multiEq.M.first())?.let { (C, F) ->
			if (multiEq.S.intersect(F.map { it.S }).isEmpty())
				return false
			else {
				this.remove(multiEq)
				this.add(MultiEquation(multiEq.S, mutableListOf(C)))
				this.addAll(F)
				return true
			}
		}
		return false
	}
	return false
}

fun MutableSet<MultiEquation>.compactify() : MutableSet<MultiEquation>{
	MergingEquivalenceClass<MultiEquation> { s, t ->
		s.S.intersect(t.S).isEmpty()
	}.let { mec ->
		this.forEach { mec.add(it) { s, t -> MultiEquation(s.S.also { it.addAll(t.S) }, s.M.also { it.addAll(t.M) }) } }
		return mec.classes
	}
}

data class MultiEquation(val S: MutableSet<FreeVariable>, val M: MutableList<Term>) {
	constructor(vararg originalTerms: Term) : this(mutableSetOf<FreeVariable>(), mutableListOf<Term>()) {
		originalTerms.fold(S.to(M))
		{ (s: Set<FreeVariable>, m: List<Term>), t: Term ->
			s.also { it.addAll(t.getFreeVariables()) }.to(m.also {
				if (t !is FreeVariable) it.add(t)
			})
		}
	}
}

private fun DEC(vararg M: Term): Pair<Term, Set<MultiEquation>>? {
	val first = M.first()
	if (M.any { it is BoundVariable } && M.any { it !== first })
		return null
	M.find { it is FreeVariable }?.let { return it.to(setOf(MultiEquation(*M))) }
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
