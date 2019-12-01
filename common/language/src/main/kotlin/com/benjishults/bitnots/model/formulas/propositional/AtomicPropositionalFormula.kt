package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Substitution

abstract class AtomicPropositionalFormula(cons: FormulaConstructor) : PropositionalFormula(cons) {

    override fun unifyUncached(other: Formula<*>, sub: Substitution): Substitution =
            if (this == other)
                sub
            else
                NotUnifiable

    override fun applySub(substitution: Substitution): Formula<FormulaConstructor> = this

    override fun contains(variable: Variable<*>, sub: Substitution) = false

    override fun getFreeVariables(): Set<FreeVariable> = emptySet()
    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> = emptySet()

    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode() = constructor.name.hashCode()
}