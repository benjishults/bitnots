package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Substitution

abstract class VarArgPropositionalFormula(cons: FormulaConstructor, vararg val formulas: Formula<*>) : PropositionalFormula(cons) {
    override fun contains(variable: Variable<*>, sub: Substitution): Boolean =
            formulas.any {
                it.contains(variable, sub)
            }

    override fun unifyUnchached(other: Formula<*>, sub: Substitution): Substitution {
        if (other::class === this::class) {
            val otherOne = other as VarArgPropositionalFormula
            generateSequence(
                    0 to otherOne.formulas.asSequence().map { otherForm ->
                        // sequence of <otherForm, unifier>
                        otherForm to Formula.unify(formulas[0], otherForm, sub)
                    }.filter {
                        it.second !== NotUnifiable
                    }) { (index, _) ->
                index + 1 to
                        otherOne.formulas.asSequence().map { otherForm ->
                            otherForm to Formula.unify(formulas[index + 1], otherForm, sub)
                        }.filter {
                            it.second !== NotUnifiable
                        }
            }.forEach { (index, sequence: Sequence<Pair<Formula<*>, Substitution>>) ->
                sequence.forEach {
                    // NOTE we know that sequence.all { formula[index].applySub(it.second) == it.first.applySub(it.second)
                    // TODO see whether formulas.filter {it === formula[index]}.
                }
            }
        }
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
