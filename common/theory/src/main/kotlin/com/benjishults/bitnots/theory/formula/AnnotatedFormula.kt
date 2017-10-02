package com.benjishults.bitnots.theory.formula

import com.benjishults.bitnots.model.formulas.Formula

sealed class AnnotatedFormula(open val name: String = "")

data class CnfAnnotatedFormula(override val name: String, val formulaRole: FormulaRoles, val clause: List<SimpleSignedFormula<*>>) : AnnotatedFormula()

data class FolAnnotatedFormula(override val name: String, val formulaRole: FormulaRoles, val formula: Formula<*>) : AnnotatedFormula()
