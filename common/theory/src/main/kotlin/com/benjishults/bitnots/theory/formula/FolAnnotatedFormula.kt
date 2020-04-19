package com.benjishults.bitnots.theory.formula

import com.benjishults.bitnots.model.formulas.Formula

class FolAnnotatedFormula(
    name: String,
    formulaRole: FormulaRole,
    formula: Formula
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
