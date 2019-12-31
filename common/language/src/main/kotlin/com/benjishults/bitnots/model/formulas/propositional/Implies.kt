package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Substitution

data class Implies(
        val antecedent: Formula<*>,
        val consequent: Formula<*>
) : Formula<FormulaConstructor>(FormulaConstructor.intern(LogicalOperator.implies.name)) {
    override fun contains(variable: Variable<*>, sub: Substitution) =
            antecedent.contains(variable, sub) || consequent.contains(variable, sub)

    override fun unifyUncached(other: Formula<*>, sub: Substitution): Substitution {
        if (other is Implies) {
            Formula.unify(antecedent, other.antecedent, sub).takeIf {
                it !== NotUnifiable
            }?.let {
                Formula.unify(consequent, other.consequent, it).takeIf {
                    it != NotUnifiable
                }?.let {
                    return it
                }
            }
        }
        return NotUnifiable
    }

    override fun getFreeVariables(): Set<FreeVariable> =
            antecedent.getFreeVariables().union(consequent.getFreeVariables())

    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> =
            antecedent.getVariablesUnboundExcept(boundVars).union(consequent.getVariablesUnboundExcept(boundVars))

    override fun applySub(substitution: Substitution): Implies =
            Implies(antecedent.applySub(substitution), consequent.applySub(substitution))

    override fun toString() = "(${constructor.name} ${antecedent} ${consequent})"

}
