package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaWithSubformulas
import com.benjishults.bitnots.model.formulas.PropositionalFormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.NotCompatible
import com.benjishults.bitnots.model.unifier.Substitution

data class Implies(
        val antecedent: Formula,
        val consequent: Formula
) : FormulaWithSubformulas(PropositionalFormulaConstructor.intern(LogicalOperator.implies.name), antecedent,
                           consequent) {
    override fun contains(variable: Variable, sub: Substitution) =
            antecedent.contains(variable, sub) || consequent.contains(variable, sub)

    override fun unifyUncached(other: Formula, sub: Substitution): Substitution {
        if (other is Implies) {
            Formula.unify(antecedent, other.antecedent, sub).takeIf {
                it !== NotCompatible
            }?.let {
                Formula.unify(consequent, other.consequent, it).takeIf {
                    it != NotCompatible
                }?.let {
                    return it
                }
            }
        }
        return NotCompatible
    }

    override fun getFreeVariables(): Set<FreeVariable> =
            antecedent.getFreeVariables().union(consequent.getFreeVariables())

    override fun getVariablesUnboundExcept(boundVars: List<Variable>): Set<Variable> =
            antecedent.getVariablesUnboundExcept(boundVars).union(consequent.getVariablesUnboundExcept(boundVars))

    override fun applySub(substitution: Substitution): Implies =
            Implies(antecedent.applySub(substitution), consequent.applySub(substitution))

    override fun applyPair(pair: Pair<Variable, Term>): Implies =
            Implies(antecedent.applyPair(pair), consequent.applyPair(pair))

    override fun toString() = "(${constructor.name} ${antecedent} ${consequent})"

}
