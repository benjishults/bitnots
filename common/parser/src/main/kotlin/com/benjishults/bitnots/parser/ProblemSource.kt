package com.benjishults.bitnots.parser

import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaForm

interface ProblemSource {
    val abbreviation: String
    fun <F : FormulaForm, AF : AnnotatedFormula> parser(form: F): Parser<AF, *>
}
