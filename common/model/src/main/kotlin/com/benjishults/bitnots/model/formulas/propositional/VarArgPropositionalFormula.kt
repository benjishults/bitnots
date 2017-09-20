package com.benjishults.bitnots.model.formulas.propositional

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.FormulaConstructor
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Variable
import com.benjishults.bitnots.model.unifier.Substitution

abstract class VarArgPropositionalFormula : PropositionalFormula {

    lateinit var formulas: List<Formula<*>> private set

    constructor(cons: FormulaConstructor,
                formulas: List<Formula<*>>) : super(cons) {
        this.formulas = formulas
    }

    override fun contains(variable: Variable<*>, sub: Substitution): Boolean {
        TODO()
    }

    override fun unify(other: Formula<*>, sub: Substitution): Substitution {
        TODO()
//		formulas.map { it.applySub(substitution) }.toTypedArray())

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

    override fun applySubDestructive(substitution: Substitution): VarArgPropositionalFormula =
            this.also {
                formulas.map { it.applySub(substitution) }.toList()
            }

    override fun toString() = "(${constructor.name} ${formulas.joinToString(" ")})"
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
