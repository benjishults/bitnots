package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.util.InternTable

class FreeVariable private constructor(name: String) : Variable(name) {
	override fun getFreeVariables(): Set<FreeVariable> = setOf(this)

	companion object inner : InternTable<FreeVariable, Nothing>({ name, _ -> FreeVariable(name) })

}

fun FV(name: String): FreeVariable = FreeVariable.intern(name)
