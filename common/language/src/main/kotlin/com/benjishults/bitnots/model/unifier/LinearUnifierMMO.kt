package com.benjishults.bitnots.model.unifier

//fun Term<*>.linearUnify(other: Term<*>): Substitution = SystemOfMultiEquations(this, other).solve()

//val BAD: Int = 5

//data class SystemOfMultiEquations private constructor(
//		val U: MutableList<MultiEquation>,
//		val T: MutableList<MultiEquation> = mutableListOf(),
//		private val dummy: FreeVariable = FreeVariable.new("TEMP"),
//		private val freeVarsToCountMap: MutableMap<FreeVariable, Int> = mutableMapOf(),
//		private val freeVarsToMultiEqn: MutableMap<FreeVariable, MultiEquation> = mutableMapOf()) {
//
//	constructor(
//			vararg originalTerms: Term<*>) : this(mutableListOf<MultiEquation>()) {
//		originalTerms.fold(freeVarsToCountMap) { counts, t ->
//			counts.also { counts ->
//				t.getFreeVariablesAndCounts().entries.forEach { (v, count) ->
//					counts.get(v)?.also {
//						counts.put(v, count + it)
//					} ?: counts.put(v, count)
//				}
//			}
//		}.mapTo(U) { (v, c) ->
//			MultiEquation(mutableSetOf(v), mutableListOf<Term<*>>(), c);
//		}.also {
//			it.add(MultiEquation(mutableSetOf(FreeVariable.new("x")), mutableListOf(*originalTerms), 0))
//		}
//	}
//
//	tailrec fun solve(): Substitution {
//		val addToT = reduce()
//		if (addToT !== null) {
//			compactify(addToT)
////			val addToT = mutableListOf<MultiEquation>()
////			U.removeIf { multiEq ->
////				multiEq.M.singleOrNull()?.takeIf { it == C }?.let {
////					addToT.add(multiEq)
////					return@removeIf true
////				} ?: false
////			}
//			T.add(addToT)
//			return solve()
//		}
//		T.addAll(U)
//		return toSub()
//	}
//
//	private fun toSub(): Substitution {
//		var s: Substitution = EmptySub
//		for (index in T.size - 1 downTo 0) {
//			val multiEq = T[index]
//			// is it possible for the S containing dummy to also contain other variables?  I don't think so.
//			if (multiEq.M.isNotEmpty() && dummy !in multiEq.S) {
//				// Does this have to have single M?  Certainly, S does not have to be a singleton.
//				val term = multiEq.M.single().applySub(s)
//				s = s.compose(Sub(*multiEq.S.map { it.to(term) }.toTypedArray()))
//			}
//		}
//		return NotUnifiable
//	}
//
//	// U' = (U - { S=M }) U { S=(C) } U F
//	/**
//	 * @return the list of MultiEquations that need to be transferred from U to T
//	 */
//	private fun reduce(): MultiEquation? {
//		U.asSequence().filter {
//			it.counter == 0
//		}.firstOrNull()?.let { firstNonEmptyMultiEqn ->
//			if (firstNonEmptyMultiEqn.M.isEmpty()) {
//				// no variables from elsewhere are being removed so no count changes needed
//				U.remove(firstNonEmptyMultiEqn)
//				return firstNonEmptyMultiEqn
//			} else {
//				computeCommonPartAndFrontier(firstNonEmptyMultiEqn.M.first())?.let { (C, F) ->
//					if (firstNonEmptyMultiEqn.S.any { sVar ->
//						F.any {
//							sVar in it.S
//						}
//					})
//						return null
//					else {
//						U.remove(firstNonEmptyMultiEqn)
//						// decrement S=M for each variable in S that occurs in the LHS of an element of F.
//						U.forEach { meq ->
//							meq.S.forEach { sVar ->
//								if (F.any {
//									sVar in it.S
//								})
//									meq.counter--
//							}
//						}
//						U.addAll(F)
//						// Putting 0 here because these are about to move to T so I don't think it matters
//						return MultiEquation(firstNonEmptyMultiEqn.S, mutableListOf(C), 0)
//					}
//				} ?: return null
//			}
//		} ?: return null
//	}
//
//	private fun compactify(other: MultiEquation): MutableList<MultiEquation> {
//		MergingEquivalenceClass<MultiEquation> { s, t ->
//			s.S.intersect(t.S).isEmpty()
//		}.let { mec ->
//			val merge: (MultiEquation, MultiEquation) -> MultiEquation = { me1, me2 ->
//				MultiEquation(
//						me1.S.also {
//							it.addAll(me2.S)
//						},
//						me1.M.also {
//							it.addAll(me2.M)
//						},
//						me1.counter + me2.counter)
//			}
//			U.asSequence().plus(other).forEach {
//				// TODO is other really needed?
//				mec.add(it, merge)
//			}
//			return mec.classes.toMutableList().also { it.sort() }
//		}
//	}
//
//	private data class CommonPartAndFrontier(
//			val commonPart: Term<*>,
//			val frontier: Set<MultiEquation>)
//
//	// DEC algorithm
//	private fun computeCommonPartAndFrontier(vararg M: Term<*>): CommonPartAndFrontier? {
//		val first = M.first()
//		if (M.any { it is BoundVariable } && M.any { it !== first })
//			return null
//		M.find { it is FreeVariable }?.let {
//			// TODO decrement counts of other MultiEquations whose variables occur in the LHS of M
//			M.forEach { }
//			return CommonPartAndFrontier(it, setOf(MultiEquation(*M)))
//		}
//		if (M.all { it.cons == first.cons }) {
//			if (first is BoundVariable || (first is Function && first.arguments.isEmpty())) {
//				return CommonPartAndFrontier(first, emptySet())
//			} else {
//				val DECs = mutableListOf<CommonPartAndFrontier>()
//				for (i in 0..(first as Function).arguments.size - 1) {
//					DECs.add(computeCommonPartAndFrontier(*M.map {
//						(it as Function).arguments[i]
//					}.toTypedArray()) ?: return null)
//				}
//				return CommonPartAndFrontier(
//						Fn(first.cons.name, first.cons.arity)( DECs.map { it.commonPart }.toTypedArray()),
//						DECs.fold<CommonPartAndFrontier, MutableSet<MultiEquation>>(mutableSetOf<MultiEquation>()) {
//							frontier, commPAFrontier ->
//							frontier.also {
//								it.addAll(commPAFrontier.frontier)
//							}
//						})
//			}
//		} else
//			return null
//	}
//
//	// called by (1) original construction of U (2) compactify and (3) during reduce
//	private inner class MultiEquation(
//			val S: MutableSet<FreeVariable>,
//			var M: MutableList<Term<*>>,
//			var counter: Int) : Comparable<MultiEquation> {
//
//		// called to compute frontier
//		constructor(vararg originalTerms: Term<*>) : this(mutableSetOf<FreeVariable>(), mutableListOf<Term<*>>(), 0) {
//			originalTerms.fold(S.to(M)) { (s: MutableSet<FreeVariable>, m: MutableList<Term<*>>), t: Term<*> ->
//				s.also {
//					it.addAll(t.getFreeVariables())
//				}.to(m.also {
//					if (t !is FreeVariable) it.add(t)
//				})
//			}
//			// TODO is this right?
//			counter = S.fold(0) { count, v ->
//				count + freeVarsToCountMap.getValue(v)
//			}
//		}
//
//		override fun compareTo(other: MultiEquation): Int = counter.compareTo(other.counter)
//
//		override fun toString() : String {
//			return "[${counter}] ${S}=${M}"
//		}
//	}
//
//}
