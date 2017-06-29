package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.unifier.Substitution

abstract class Term(val cons: TermConstructor) {
	abstract fun unify(other: Term): Substitution?
	abstract fun applySub(substitution: Substitution) : Term

	abstract fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable>
	abstract fun getFreeVariables(): Set<FreeVariable>
}
