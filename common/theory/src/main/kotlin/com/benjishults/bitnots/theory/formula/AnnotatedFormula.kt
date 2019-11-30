package com.benjishults.bitnots.theory.formula

import com.benjishults.bitnots.model.formulas.Formula

sealed class AnnotatedFormula(
    val name: String,
    val formulaRole: FormulaRoles
)

class CnfAnnotatedFormula(
    name: String,
    formulaRole: FormulaRoles,
    val clause: List<SimpleSignedFormula<*>>
) : AnnotatedFormula(name, formulaRole) {
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
}
