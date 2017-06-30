package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.unifier.Substitution.EmptySubstitution

abstract class Term(val cons: TermConstructor) {
	abstract fun unify(other: Term, sub: Substitution = EmptySubstitution): Substitution
	abstract fun applySub(substitution: Substitution) : Term

	abstract fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable>
	abstract fun getFreeVariables(): Set<FreeVariable>
}
