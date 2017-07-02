package com.benjishults.bitnots.model.util

import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Fun
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Sub
import com.benjishults.bitnots.model.unifier.Substitution

fun linearUnify(t1: Term, t2: Term): Substitution = SystemOfMultiEquations(t1, t2).solve()

val BAD: Int = 5

data class SystemOfMultiEquations private constructor(
		val U: MutableList<MultiEquation>,
		val T: MutableList<MultiEquation> = mutableListOf(),
		val dummy: FreeVariable) {

	private constructor(
			U: MutableList<MultiEquation>,
			dummy: FreeVariable) : this(U, mutableListOf(), dummy)

	constructor(vararg originalTerms: Term) : this(mutableListOf<MultiEquation>(), FreeVariable.new("TEMP")) {
		originalTerms.fold(mutableMapOf<FreeVariable, Int>()) { counts, t ->
			counts.also { counts ->
				t.getFreeVariablesAndCounts().entries.forEach { (v, count) ->
					counts.get(v)?.also {
						counts.put(v, count + it)
					} ?: counts.put(v, count)
				}
			}
		}.mapTo(U) { (v, c) ->
			MultiEquation(mutableSetOf(v), mutableListOf<Term>(), c);
		}.also {
			it.add(MultiEquation(mutableSetOf(FreeVariable.new("x")), mutableListOf(*originalTerms), 0))
		}
	}

	tailrec fun solve(): Substitution {
		U.reduce()?.let { (multiEq, C) ->
			U.compactify()
			Sub(*(multiEq.S.map { it.to(C) }.toTypedArray())).let { sub ->
				U.forEach { it.M = it.M.mapTo(mutableListOf()) { it.applySub(sub) } }
			}
			val addToT = mutableListOf<MultiEquation>()
			U.removeIf { multiEq ->
				multiEq.M.singleOrNull()?.takeIf { it == C }?.let {
					addToT.add(multiEq)
					return@removeIf true
				} ?: false
			}
			T.addAll(addToT)
			return solve()
		} ?: return T.also { it.addAll(U) }.toSub(dummy)
	}
}

/**
 * This destructively modifies the receiver by reversing it.
 */
fun MutableList<MultiEquation>.toSub(dummy: FreeVariable): Substitution {
	this.also { it.reverse() }.fold(EmptySub as Substitution) { s, multiEq ->
		// is it possible for the S containing dummy to also contain other variables?  I don't think so.
		if (multiEq.M.isNotEmpty() && dummy !in multiEq.S) {
			// Does this have to have single M?  Certainly, S does not have to be a singleton.
			val term = multiEq.M.single().applySub(s)
			s.compose(Sub(*multiEq.S.map { it.to(term) }.toTypedArray()))
		} else
			s
	}
	return NotUnifiable
}

fun MutableList<MultiEquation>.reduce(): Pair<MultiEquation, Term>? {
	// find S,M whose vars in S do not occur elsewhere in U
	this.asSequence().filter { it.M.isNotEmpty() }.firstOrNull()?.let {
		multiEq ->
		DEC(multiEq.M.first())?.let { (C, F) ->
			if (multiEq.S.intersect(F.map { it.S }).isEmpty())
				return null
			else {
				this.remove(multiEq)
				this.add(MultiEquation(multiEq.S, mutableListOf(C), BAD))
				this.addAll(F)
				return multiEq.to(C)
			}
		} ?: return null
	} ?: return null
}

// TODO may be able to make this more efficient by passing in the new/changed
fun MutableList<MultiEquation>.compactify(): MutableList<MultiEquation> {
	MergingEquivalenceClass<MultiEquation> { s, t ->
		s.S.intersect(t.S).isEmpty()
	}.let { mec ->
		this.forEach { mec.add(it) { s, t -> MultiEquation(s.S.also { it.addAll(t.S) }, s.M.also { it.addAll(t.M) }, BAD) } }
		return mec.classes.toMutableList().also { it.sort() }
	}
}

data class MultiEquation(val S: MutableSet<FreeVariable>, var M: MutableList<Term>, var counter: Int) : Comparable<MultiEquation> {
	override fun compareTo(other: MultiEquation): Int = counter.compareTo(other.counter)

	constructor(vararg originalTerms: Term) : this(mutableSetOf<FreeVariable>(), mutableListOf<Term>(), 0) {
		originalTerms.fold(S.to(M))
		{ (s: Set<FreeVariable>, m: List<Term>), t: Term ->
			s.also { it.addAll(t.getFreeVariables()) }.to(m.also {
				if (t !is FreeVariable) it.add(t)
			})
		}
	}
}

data class CommonPartAndFrontier(val commonPart: Term, val frontier: Set<MultiEquation>)

private fun DEC(vararg M: Term): CommonPartAndFrontier? {
	val first = M.first()
	if (M.any { it is BoundVariable } && M.any { it !== first })
		return null
	M.find { it is FreeVariable }?.let {
		return CommonPartAndFrontier(it, setOf(MultiEquation(*M)))
	}
	if (M.all { it.cons == first.cons }) {
		if (first is BoundVariable || (first is Function && first.arguments.isEmpty())) {
			return CommonPartAndFrontier(first, emptySet())
		} else {
			val DECs = mutableListOf<CommonPartAndFrontier>()
			for (i in 0..(first as Function).arguments.size) {
				DECs.add(DEC(*(M as Array<Function>).map { it.arguments[i] }.toTypedArray()) ?: return null)
			}
			return CommonPartAndFrontier(
					Fun(first.cons.name, *DECs.map { it.commonPart }.toTypedArray()),
					DECs.fold(emptySet()) { s, t -> s.union(t.frontier) })
		}
	} else
		return null
}
