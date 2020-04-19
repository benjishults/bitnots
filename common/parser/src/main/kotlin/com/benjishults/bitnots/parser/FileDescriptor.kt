package com.benjishults.bitnots.parser

import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaForm

interface FileDescriptor<F : FormulaForm, S : ProblemSource> {

    val source: S
    val form: F

    fun toFileName(): String

    fun <AF : AnnotatedFormula> parser(): Parser< AF, *> =
        source.parser(form)

}
