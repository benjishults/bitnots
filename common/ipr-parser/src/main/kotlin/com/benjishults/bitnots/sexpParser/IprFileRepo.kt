package com.benjishults.bitnots.sexpParser

import com.benjishults.bitnots.parser.Parser
import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FOF
import com.benjishults.bitnots.theory.formula.FormulaForm

object IprFileRepo : ProblemSource {
    override val abbreviation: String = "IPR"
    override fun <F : FormulaForm, AF : AnnotatedFormula> parser(form: F): Parser<AF, *> =
        when (form) {
            is FOF  -> SexpParser as Parser<AF, *>
            else -> throw IllegalArgumentException()
        }

}
