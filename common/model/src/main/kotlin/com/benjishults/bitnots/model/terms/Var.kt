package com.benjishults.bitnots.model.terms

import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Sub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.util.InternTable

sealed class Variable(name: TermConstructor) : Term(name) {

	override fun contains(variable: Variable): Boolean = this === variable

	override fun applySub(substitution: Substitution): Term = substitution.applyToVar(this)

	override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> = if (this in boundVars) setOf() else setOf(this)

	override fun toString(): String {
		return cons.name
	}
}

class BoundVariable private constructor(name: String) : Variable(BVConstructor(name)) {
	
	class BVConstructor(name:String) : TermConstructor(name)

	override fun unify(other: Term, sub: Substitution): Substitution = if (other === this) sub else NotUnifiable

	override fun getFreeVariables(): Set<FreeVariable> = emptySet()

	companion object inner : InternTable<BoundVariable, Nothing>({ name, _ -> BoundVariable(name) })

}

fun BV(name: String): BoundVariable = BoundVariable.intern(name)

class FreeVariable private constructor(name: String) : Variable(FVConstructor(name)) {

	class FVConstructor(name:String) : TermConstructor(name)

		override fun unify(other: Term, sub: Substitution): Substitution =
			if (this === other)
				sub
			else if (other.contains(this))
				NotUnifiable
			else
				sub.compose(Sub(this.to(other)))

	override fun getFreeVariables(): Set<FreeVariable> = setOf(this)

	companion object inner : InternTable<FreeVariable, Nothing>({ name, _ -> FreeVariable(name) })

}

fun FV(name: String): FreeVariable = FreeVariable.intern(name)
