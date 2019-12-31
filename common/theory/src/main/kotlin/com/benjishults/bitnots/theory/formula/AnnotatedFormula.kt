package com.benjishults.bitnots.theory.formula

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.util.isCnf

open class AnnotatedFormula(
        val name: String,
        val formulaRole: FormulaRole
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
        formulaRole: FormulaRole,
        val clause: Formula<*>
) : AnnotatedFormula(name, formulaRole) {

    init {
        require(clause.isCnf())
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
        formulaRole: FormulaRole,
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
