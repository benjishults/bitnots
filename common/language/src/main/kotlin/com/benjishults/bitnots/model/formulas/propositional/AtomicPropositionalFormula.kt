package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.PropositionalFormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.NotCompatible
import com.benjishults.bitnots.model.unifier.Substitution

abstract class AtomicPropositionalFormula(
        override val constructor: PropositionalFormulaConstructor
) : Formula<PropositionalFormulaConstructor> {

    override fun unifyUncached(other: Formula<*>, sub: Substitution): Substitution =
            if (this == other)
                sub
            else
                NotCompatible

    override fun applySub(substitution: Substitution): Formula<PropositionalFormulaConstructor> = this
    override fun applyPair(pair: Pair<Variable<*>, Term<*>>): Formula<PropositionalFormulaConstructor> = this

    override fun contains(variable: Variable<*>, sub: Substitution) = false

    override fun getFreeVariables(): Set<FreeVariable> = emptySet()
    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> = emptySet()

    override fun equals(other: Any?): Boolean =
            other is AtomicPropositionalFormula && other.constructor === this.constructor

    override fun hashCode() = constructor.name.hashCode()
    override fun toString() = "(${constructor.name})"

}
