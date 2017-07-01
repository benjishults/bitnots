package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Sub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.util.InternTable

class Function private constructor(name: String, vararg val arguments: Term) : Term(FunctionConstructor(name)) {

	class FunctionConstructor(name: String) : TermConstructor(name) {
		// TODO look into doing it this way
//		operator fun invoke(vararg arguments:Term) {
//			
//		}
	}

	override fun contains(variable: Variable): Boolean = arguments.any { it.contains(variable) }

	override fun unify(other: Term, sub: Substitution): Substitution {
		if (other is Function && other.cons === cons) {
			return sub.compose(arguments.foldIndexed(EmptySub as Substitution) { i, s, t ->
				t.unify(other.applySub(s).arguments[i].applySub(s), s).let {
					when (it) {
						NotUnifiable -> return NotUnifiable
						else -> it
					}
				}
			})
		} else if (other is FreeVariable)
			return sub.compose(Sub(other.to(this)))
		else
			return NotUnifiable
	}

	override fun getFreeVariables(): Set<FreeVariable> =
			arguments.fold(emptySet<FreeVariable>()) { s, t -> s.union(t.getFreeVariables()) }

	override fun applySub(substitution: Substitution): Function {
		return Fun(cons.name, *arguments.map { it.applySub(substitution) }.toTypedArray())
	}

	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> {
		val value = mutableSetOf<Variable>()
		arguments.map { value.addAll(it.getVariablesUnboundExcept(boundVars)) }
		return value.toSet()
	}

	companion object inner : InternTable<Function, Term>({ name, args -> Function(name, *args) })

	override fun toString() = "(${cons.name}${if (arguments.size == 0) "" else " "}${arguments.joinToString(" ")})"

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
