package com.benjishults.bitnots.model.unifier

import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable

class Substitution(val map: Map<Variable, Term>) {

	constructor(vararg pairs: Pair<Variable, Term>) : this(mapOf(*pairs))

	fun compose(other: Substitution?): Substitution? {
		TODO()
		other?.let { return other }
		return null
	}
}