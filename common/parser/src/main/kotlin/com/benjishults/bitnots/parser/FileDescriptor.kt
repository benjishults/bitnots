package com.benjishults.bitnots.parser

import com.benjishults.bitnots.theory.formula.FormulaForm

interface FileDescriptor {
    val source: ProblemSource
    val form: FormulaForm
    fun toFileName(): String
}
