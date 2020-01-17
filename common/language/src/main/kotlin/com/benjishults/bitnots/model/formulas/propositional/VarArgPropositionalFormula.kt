package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaWithSubformulas
import com.benjishults.bitnots.model.formulas.PropositionalFormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.NotCompatible
import com.benjishults.bitnots.model.unifier.Substitution

abstract class VarArgPropositionalFormula(cons: PropositionalFormulaConstructor, vararg formulas: Formula<*>) :
        FormulaWithSubformulas<PropositionalFormulaConstructor>(cons, *formulas) {

    override fun contains(variable: Variable<*>, sub: Substitution): Boolean =
            formulas.any {
                it.contains(variable, sub)
            }

    // Returns a real substitution if there is a unifiable correspondence between the elements of rest and those of remainingOthers under sub
    private fun unifyHelper(rest: Iterable<Formula<*>>, remainingOthers: List<Formula<*>>,
                            sub: Substitution): Substitution =
            rest.firstOrNull()?.let { first ->
                remainingOthers.forEach { otherForm ->
                    Formula.unify(first, otherForm, sub).let {
                        if (it !== NotCompatible) {
                            unifyHelper(rest.drop(1), remainingOthers - otherForm, it).let {
                                if (it !== NotCompatible)
                                    return it
                            }
                        }
                    }
                }
                NotCompatible
            } ?: sub

    override fun unifyUncached(other: Formula<*>, sub: Substitution): Substitution =
            if (other::class === this::class) {
                val otherOne = other as VarArgPropositionalFormula
                unifyHelper(Iterable(formulas::iterator), otherOne.formulas.asList(), sub)
            } else {
                NotCompatible
            }

    override fun getFreeVariables(): Set<FreeVariable> =
            formulas.fold(emptySet<FreeVariable>()) { s, t -> s.union(t.getFreeVariables()) }

    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> {
        val value = mutableSetOf<Variable<*>>()
        formulas.map { value.addAll(it.getVariablesUnboundExcept(boundVars)) }
        return value.toSet()
    }

    override fun applySub(substitution: Substitution): VarArgPropositionalFormula {
        val constructor = this::class.constructors.first()
        return constructor.call(formulas.map { it.applySub(substitution) }.toTypedArray())
    }

    override fun applyPair(pair: Pair<Variable<*>, Term<*>>): VarArgPropositionalFormula {
        val constructor = this::class.constructors.first()
        return constructor.call(formulas.map { it.applyPair(pair) }.toTypedArray())
    }

    override fun toString() = "(${constructor.name} ${formulas.joinToString(" ")})"
    override fun hashCode() = formulas.contentHashCode() + this::class.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other === null) return false
        if (other::class === this::class) {
            return (other as VarArgPropositionalFormula).formulas.size == this.formulas.size &&
                   other.formulas.all {
                       it in this.formulas
                   }
        }
        return false
    }
}
