package com.benjishults.bitnots.model.terms

abstract class Term(val cons: TermConstructor) {
	abstract fun substitute(map: Map<Variable, Term>): Term
	abstract fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable>
	abstract fun getFreeVariables(): Set<FreeVariable>
}
