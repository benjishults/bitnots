package com.benjishults.bitnots.theory.formula

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.propositional.PropositionalFormula
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable

sealed class AnnotatedFormula(
        val name: String,
        val formulaRole: FormulaRoles
) {
    override fun toString(): String {
        return "AnnotatedFormula(name='$name', formulaRole=$formulaRole)"
    }
}

/**
 * @param clause must be a clause
 */
class CnfAnnotatedFormula(
        name: String,
        formulaRole: FormulaRoles,
        val clause: Formula<*>
) : AnnotatedFormula(name, formulaRole) {

    init {
        require(isClause(clause))
    }

    companion object {
        public fun isClause(formula: Formula<*>): Boolean =
                formula is Predicate ||
                        formula is PropositionalVariable ||
                        (formula is Not &&
                                (formula.argument is Predicate ||
                                        formula.argument is PropositionalFormula)) ||
                        (formula is Or &&
                                formula.formulas.all {
                                    it is Predicate ||
                                            it is PropositionalVariable ||
                                            (it is Not &&
                                                    (it.argument is Predicate ||
                                                            it.argument is PropositionalVariable))
                                })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CnfAnnotatedFormula

        if (formulaRole != other.formulaRole) return false
        if (clause != other.clause) return false

        return true
    }

    override fun hashCode(): Int {
        var result = formulaRole.hashCode()
        result = 31 * result + clause.hashCode()
        return result
    }

    override fun toString(): String {
        return "CnfAnnotatedFormula(clause=$clause, ${super.toString()})"
    }
}

class FolAnnotatedFormula(
        name: String,
        formulaRole: FormulaRoles,
        val formula: Formula<*>
) : AnnotatedFormula(name, formulaRole) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FolAnnotatedFormula

        if (formulaRole != other.formulaRole) return false
        if (formula != other.formula) return false

        return true
    }

    override fun hashCode(): Int {
        var result = formulaRole.hashCode()
        result = 31 * result + formula.hashCode()
        return result
    }

    override fun toString(): String {
        return "FolAnnotatedFormula(formula=$formula, ${super.toString()})"
    }
}
