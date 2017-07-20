package com.benjishults.bitnots.model.unifier

import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.util.DirectedGraph
import com.benjishults.bitnots.model.util.EquivalenceClasses

//fun Term<*>.linearUnifyMM(other: Term<*>): Substitution = SystemOfMultiEquations(this, other).solve()
//
//val BAD: Int = 5
//
//class SystemOfMultiEquations {
//
//	val setOfMultiEqns: MutableList<MutableSet<Term<*>>> = mutableListOf()
//
//	constructor(s: Term<*>, t: Term<*>) {
//		setOfMultiEqns.add(mutableSetOf(s, t))
//	}
//
//	fun solve(): Substitution {
//		while (true) {
//			while (merge()) {
//			}
//			findEqnW2NonVariables()?.let {
//				val (M, s, t) = it
//				disagreementSet(s, t)?.let {
//					M.remove(t)
//					setOfMultiEqns.addAll(it)
//				} ?: return NotUnifiable
//			} ?: break
//		}
//		val dag = DirectedGraph(2)
//		val R = { v1: FreeVariable, v2: FreeVariable ->
//			setOfMultiEqns.any { M ->
//				M.any {
//					v1.occursIn(it)
//				} && M.any {
//					v2.occursIn(it)
//				}
//			}
//		}
//		val classes = EquivalenceClasses<FreeVariable>(R)
//		setOfMultiEqns.map { M ->
//			M.firstOrNull {
//				it !is FreeVariable
//			}?.let { t ->
//				M.first {
//					it is FreeVariable
//				}.let { x ->
//					t.getFreeVariables().map { y ->
////						dag.add(classes.add(y).to(classes.add(x)))
//					}
//				}
//			}
//		}
//		return NotUnifiable
//	}
//
//	private fun findEqnW2NonVariables(): Triple<MutableSet<Term<*>>, Term<*>, Term<*>>? {
//
//		setOfMultiEqns.any { m ->
//			var ls: Term<*>? = null
//			var tset = false
//			m.any { term ->
//				if (term !is FreeVariable) {
//					if (ls === null) {
//						ls = term
//						false
//					} else if (tset) {
//						return Triple(m, ls!!, term)
//					} else
//						false
//				} else {
//					false
//				}
//			}
//		}
//		return null
//	}
//
//	companion object inner {
//		fun disagreementSet(s: Term<*>, t: Term<*>): MutableList<MutableSet<Term<*>>>? {
//			when {
//				s === t ->
//					return mutableListOf()
//				s is FreeVariable || t is FreeVariable -> return mutableListOf(mutableSetOf(s, t))
//				s is Function && t is Function && s.cons === t.cons ->
//					return s.arguments.foldIndexed(mutableListOf<MutableSet<Term<*>>>()) { i, dis, si ->
//						disagreementSet(si, t.arguments[i])?.let { localDis ->
//							dis.also { it.addAll(localDis) }
//						} ?: return null
//						dis
//					}
//				else -> return null
//			}
//		}
//
//		fun toSub(bigM: MutableList<MutableSet<Term<*>>>): Substitution {
//
//			return NotUnifiable
//		}
//
//	}
//
//	private fun merge(): Boolean {
//		setOfMultiEqns.mapIndexed { i, set ->
//			val vars = set.filter { it !is FreeVariable }
//			for (index in i + 1..setOfMultiEqns.size - 1) {
//				setOfMultiEqns[index].map {
//					if (it is FreeVariable && it in vars) {
//						setOfMultiEqns.removeAt(index)
//						setOfMultiEqns.removeAt(i)
//						setOfMultiEqns.add(set.also { it.addAll(setOfMultiEqns[index]) })
//						return true
//					}
//				}
//			}
//		}
//		return false
//	}
//
//}
