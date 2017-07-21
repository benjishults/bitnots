package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.Substitution

abstract class PropositionalFormula(cons: FormulaConstructor) : Formula<FormulaConstructor>(cons) {
    override fun applySub(substitution: Substitution): Formula<FormulaConstructor> {
        TODO()
    }

    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> {
        TODO()
    }

    override fun getFreeVariables(): Set<FreeVariable> {
        TODO()
    }

    override fun unify(other: Formula<*>, sub: Substitution): Substitution {
        TODO()
    }

    override fun contains(variable: Variable<*>, sub: Substitution): Boolean {
        TODO()
    }

    override abstract fun equals(other: Any?): Boolean
}
