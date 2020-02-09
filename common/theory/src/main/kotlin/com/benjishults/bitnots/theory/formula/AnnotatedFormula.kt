package com.benjishults.bitnots.theory.formula

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.util.isCnf
import kotlinx.serialization.Serializable

@Serializable
open class AnnotatedFormula(
        val name: String,
        val formulaRole: FormulaRole,
        open val formula: Formula
) {
    override fun toString(): String {
        return "AnnotatedFormula(name='$name', formulaRole=$formulaRole)"
    }
}

class CnfAnnotatedFormula(
        name: String,
        formulaRole: FormulaRole,
        override val formula: Formula
) : AnnotatedFormula(name, formulaRole, formula) {

    init {
        require(formula.isCnf())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CnfAnnotatedFormula

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
        return "CnfAnnotatedFormula(formula=$formula, ${super.toString()})"
    }

}

class FolAnnotatedFormula(
        name: String,
        formulaRole: FormulaRole,
        override val formula: Formula
) : AnnotatedFormula(name, formulaRole, formula) {


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
