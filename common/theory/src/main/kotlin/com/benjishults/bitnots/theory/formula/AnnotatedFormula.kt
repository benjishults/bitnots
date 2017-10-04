package com.benjishults.bitnots.theory.formula

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.theory.problem.ProblemSource

sealed class AnnotatedFormula(
//        open val source: ProblemSource = ProblemSource.bitnots,
        open val name: String = ""//,
//        open val sourceDetail: String = ""
)

data class CnfAnnotatedFormula(
        override val name: String,
        val formulaRole: FormulaRoles,
        val clause: List<SimpleSignedFormula<*>>/*,
        override val source: ProblemSource = ProblemSource.bitnots,
        override val sourceDetail: String = ""*/
) : AnnotatedFormula()

data class FolAnnotatedFormula(
        override val name: String,
        val formulaRole: FormulaRoles,
        val formula: Formula<*>/*,
        override val source: ProblemSource = ProblemSource.bitnots,
        override val sourceDetail: String = ""*/
) : AnnotatedFormula()
