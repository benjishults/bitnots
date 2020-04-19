package com.benjishults.bitnots.theory.formula

import com.benjishults.bitnots.model.formulas.Formula

open class AnnotatedFormula(
    val name: String,
    val formulaRole: FormulaRole,
    val formula: Formula
) {
    override fun toString(): String {
        return "AnnotatedFormula(name='$name', formulaRole=$formulaRole)"
    }
}
