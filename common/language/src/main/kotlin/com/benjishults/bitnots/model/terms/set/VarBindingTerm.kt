package com.benjishults.bitnots.model.terms.set

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.*
import com.benjishults.bitnots.model.unifier.Substitution
import kotlin.reflect.KParameter

abstract class VarBindingTerm(cons: TermConstructor, val variable: BoundVariable, val formula: Formula) :  Term(cons) {

    override fun unifyUncached(other:  Term, sub: Substitution): Substitution {
        TODO()
    }

    override fun getFreeVariables(): Set<FreeVariable> =
            formula.getFreeVariables()

    override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> {
        return formula.getVariablesUnboundExcept(boundVars.plus(variable))
    }

    override fun applySub(substitution: Substitution):  Term {
        return this::class.constructors.first().let { cons ->
            cons.parameters.find { it.name == "formula" }?.let { formulaParam ->
                cons.parameters.find { it.name == "variable" }?.let { variableParam ->
                    cons.callBy(mapOf(formulaParam to formula.applySub(substitution), variableParam to variable))
                } ?: error("No variable parameter found")
            } ?: error("No formula parameter found")
        }
    }

    override fun toString(): String =
            "(${cons.name} (${variable}) ${formula})"

    override fun hashCode(): Int =
            variable.hashCode() + this::class.hashCode()

    override fun equals(other: Any?): Boolean {
        // FIXME make this smarter
        return this === other
    }

}
