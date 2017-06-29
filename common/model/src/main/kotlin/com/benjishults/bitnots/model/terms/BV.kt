package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.util.InternTable

class BoundVariable private constructor(name: String) : Variable(name) {
	override fun unify(other: Term): Substitution? = TODO()

	override fun getFreeVariables(): Set<FreeVariable> = emptySet()

	companion object inner : InternTable<BoundVariable, Nothing>({ name, _ -> BoundVariable(name) })

}

fun BV(name: String): BoundVariable = BoundVariable.intern(name)
