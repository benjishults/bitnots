package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaWithSubformulas
import com.benjishults.bitnots.model.formulas.PropositionalFormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.NotCompatible
import com.benjishults.bitnots.model.unifier.Substitution

data class Iff(val first: Formula<*>, val second: Formula<*>) : FormulaWithSubformulas<PropositionalFormulaConstructor>(PropositionalFormulaConstructor.intern(LogicalOperator.iff.name), first, second) {
    override fun unifyUncached(other: Formula<*>, sub: Substitution): Substitution {
        if (other is Iff) {
            Formula.unify(first, other.first, sub).takeIf {
                it !== NotCompatible
            }?.let {
                Formula.unify(second, other.second, it).takeIf {
                    it != NotCompatible
                }?.let {
                    return it
                } ?: Formula.unify(first, other.second, sub).takeIf {
                    it !== NotCompatible
                }?.let {
                    Formula.unify(second, other.first, it).takeIf {
                        it != NotCompatible
                    }?.let {
                        return it
                    }
                }
            }
        }
        return NotCompatible
    }

    override fun contains(variable: Variable<*>, sub: Substitution): Boolean =
            first.contains(variable, sub) || second.contains(variable, sub)

    override fun applySub(substitution: Substitution): Formula<PropositionalFormulaConstructor> =
            Iff(first.applySub(substitution), second.applySub(substitution))


    override fun getFreeVariables(): Set<FreeVariable> = first.getFreeVariables().union(second.getFreeVariables())

    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> =
            first.getVariablesUnboundExcept(boundVars).union(second.getVariablesUnboundExcept(boundVars))

    override fun toString(): String = "(${constructor.name} ${first} ${second})"

    override fun equals(other: Any?): Boolean {
        if (other === null) return false
        if (other::class === this::class) {
            return ((other as Iff).first == first && other.second == second) ||
                    (other.second == first && other.first == second)
        }
        return false
    }

}
