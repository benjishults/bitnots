package com.benjishults.bitnots.model.formulas.fol

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.util.InternTable

class Predicate private constructor(name: String, vararg val arguments: Term<*>) : Formula(FormulaConstructor.intern(name)) {
	override fun unify(other: Formula, sub: Substitution): Substitution {
		TODO()
	}

	override fun getFreeVariables(): Set<FreeVariable> = arguments.fold(emptySet()) { s, t -> s.union(t.getFreeVariables()) }

	override fun applySub(substitution: Substitution): Formula {
		return Predicate(constructor.name, *arguments.map { it.applySub(substitution) }.toTypedArray())
	}

	override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> {
		val value = mutableSetOf<Variable<*>>()
		arguments.map { value.addAll(it.getVariablesUnboundExcept(boundVars)) }
		return value.toSet()
	}

	companion object inner : InternTable<Predicate>({ name -> TODO() })

	override fun equals(other: Any?): Boolean {
		if (other === null) return false
		if (other::class === this::class) {
			if ((other as Predicate).arguments.size == this.arguments.size) {
				for (index in 0..this.arguments.size - 1) {
					if (other.arguments[index] != this.arguments[index])
						return false
				}
				return true
			}
		}
		return false
	}

	override fun toString() = "(${constructor.name} ${arguments.joinToString(" ")})"

}

fun Pred(name: String, vararg arguments: Term<*>): Predicate = Predicate.intern(name)
