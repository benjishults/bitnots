package com.benjishults.bitnots.model.formulas.fol

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.Substitution

abstract class VarsBindingFormula(
        override val constructor: FormulaConstructor,
        vararg val variables: BoundVariable,
        val formula: Formula
) : Formula {

    override fun contains(variable: Variable, sub: Substitution): Boolean {
        TODO()
    }

    init {
        require(variables.size > 0)
    }

    override fun unifyUncached(other: Formula, sub: Substitution): Substitution {
        TODO()
    }

    override fun getFreeVariables(): Set<FreeVariable> =
            formula.getFreeVariables()

    override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> {
        return formula.getVariablesUnboundExcept(boundVars.plus(variables))
    }

    override fun applySub(substitution: Substitution): Formula {
        return this::class.constructors.first().let { cons ->
            cons.parameters.find { it.name == "formula" }?.let { formulaParam ->
                cons.parameters.find { it.name == "variables" }?.let { variablesParam ->
                    cons.callBy(mapOf(formulaParam to formula.applySub(substitution), variablesParam to variables))
                } ?: error("No variables parameter found")
            } ?: error("No formula parameter found")
        }
    }

    override fun applyPair(pair: Pair<Variable, Term>): Formula {
        return this::class.constructors.first().let { cons ->
            cons.parameters.find { it.name == "formula" }?.let { formulaParam ->
                cons.parameters.find { it.name == "variables" }?.let { variablesParam ->
                    cons.callBy(mapOf(formulaParam to formula.applyPair(pair), variablesParam to variables))
                } ?: error("No variables parameter found")
            } ?: error("No formula parameter found")
        }
    }

    override fun toString(): String =
            "(${constructor.name} ((${variables.joinToString(") (")})) ${formula})"

    override fun hashCode(): Int =
            variables.contentHashCode() + this::class.hashCode()

    override fun equals(other: Any?): Boolean {
        // FIXME make this smarter
        return this === other
    }

}
