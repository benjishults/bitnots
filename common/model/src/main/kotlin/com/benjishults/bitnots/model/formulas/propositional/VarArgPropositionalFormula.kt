package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.unifier.EmptySub

abstract class VarArgPropositionalFormula(cons: FormulaConstructor, vararg val formulas: Formula<*>) : PropositionalFormula(cons) {
    override fun contains(variable: Variable<*>, sub: Substitution): Boolean =
            formulas.any {
                it.contains(variable, sub)
            }

    // Returns a real substitution if there is a unifiable correspondence between the elements of rest and those of remainingOthers under sub
    private fun unifyHelper(rest: Iterable<Formula<*>>, remainingOthers: List<Formula<*>>, sub: Substitution): Substitution =
            rest.firstOrNull()?.let { first ->
                remainingOthers.forEach { otherForm ->
                    Formula.unify(first, otherForm, sub).let {
                        if (it !== NotUnifiable) {
                            unifyHelper(rest.drop(1), remainingOthers - otherForm, it).let {
                                if (it !== NotUnifiable)
                                    return it
                            }
                        }
                    }
                }
                NotUnifiable
            } ?: sub

    override fun unifyUnchached(other: Formula<*>, sub: Substitution): Substitution =
            if (other::class === this::class) {
                val otherOne = other as VarArgPropositionalFormula
                unifyHelper(Iterable(formulas::iterator), otherOne.formulas.asList(), sub)
            } else {
                NotUnifiable
            }

    override fun getFreeVariables(): Set<FreeVariable> =
            formulas.fold(emptySet<FreeVariable>()) { s, t -> s.union(t.getFreeVariables()) }

    init {
        if (formulas.size < 2) throw IllegalArgumentException("Must provide at least two arguments to VarArgPropositionalFormula constructor.")
    }

    override fun getVariablesUnboundExcept(boundVars: List<Variable<*>>): Set<Variable<*>> {
        val value = mutableSetOf<Variable<*>>()
        formulas.map { value.addAll(it.getVariablesUnboundExcept(boundVars)) }
        return value.toSet()
    }

    override fun applySub(substitution: Substitution): VarArgPropositionalFormula {
        val constructor = this::class.constructors.first()
        return constructor.call(formulas.map { it.applySub(substitution) }.toTypedArray())
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
