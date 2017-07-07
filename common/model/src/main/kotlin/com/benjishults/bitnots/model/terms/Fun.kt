package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.terms.Function.FunctionConstructor
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Sub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.util.InternTableWithOther


fun Fn(name: String, arity: Int = 1) = FunctionConstructor.intern(name, arity)
fun Const(name: String) = Fn(name, 0)(emptyArray())

class Function private constructor(name: FunctionConstructor, var arguments: Array<Term<*>>) : Term<FunctionConstructor>(name) {

	class FunctionConstructor private constructor(name: String, val arity: Int = 0) : TermConstructor(name) {
		companion object inner : InternTableWithOther<FunctionConstructor, Int>({ name, arity -> FunctionConstructor(name, arity) })

		operator fun invoke(arguments: Array<Term<*>>): Function {
			check(arguments.size == arity)
			return Function(this, arguments)
		}

		operator fun invoke(argument: Term<*>): Function {
			check(1 == arity)
			return Function(this, arrayOf(argument))
		}

		operator fun invoke(arg1: Term<*>, arg2: Term<*>): Function {
			check(2 == arity)
			return Function(this, arrayOf(arg1, arg2))
		}

		operator fun invoke(arg1: Term<*>, arg2: Term<*>, arg3: Term<*>): Function {
			check(3 == arity)
			return Function(this, arrayOf(arg1, arg2, arg3))
		}

		operator fun invoke(arg1: Term<*>, arg2: Term<*>, arg3: Term<*>, arg4: Term<*>): Function {
			check(4 == arity)
			return Function(this, arrayOf(arg1, arg2, arg3, arg4))
		}

		operator fun invoke(arg1: Term<*>, arg2: Term<*>, arg3: Term<*>, arg4: Term<*>, arg5: Term<*>): Function {
			check(5 == arity)
			return Function(this, arrayOf(arg1, arg2, arg3, arg4, arg5))
		}
	}

	override fun contains(variable: Variable<*>): Boolean = arguments.any { it.contains(variable) }

	override fun unify(other: Term<*>, sub: Substitution): Substitution {
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

	override fun getFreeVariablesAndCounts(): MutableMap<FreeVariable, Int> =
			arguments.fold(mutableMapOf<FreeVariable, Int>()) { s, t ->
				t.getFreeVariablesAndCounts().entries.forEach { (v, c) ->
					s.get(v)?.also {
						s.put(v, c + it)
					} ?: s.put(v, c)
				}
				s
			}

	override fun applySub(substitution: Substitution): Function {
		return cons(arguments.map { it.applySub(substitution) }.toTypedArray())
	}

	override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> {
		val value = mutableSetOf<Variable<*>>()
		arguments.map { value.addAll(it.getVariablesUnboundExcept(boundVars)) }
		return value.toSet()
	}

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

//fun Fun(name: String, arguments: Array<Term<*>> = emptyArray()): Function = FunctionConstructor.intern(name)(arguments)
