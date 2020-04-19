package com.benjishults.bitnots.theory.formula

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.util.isCnf

class CnfAnnotatedFormula(
    name: String,
    formulaRole: FormulaRole,
    formula: Formula
) : AnnotatedFormula(name, formulaRole, formula) {

    init {
        require(formula.isCnf())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (javaClass != other?.javaClass)
            return false

        other as CnfAnnotatedFormula

        if (formulaRole != other.formulaRole)
            return false
        if (formula != other.formula)
            return false

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
