package com.benjishults.bitnots.parser

import com.benjishults.bitnots.theory.formula.FormulaForm

interface FileDescriptor<out F: FormulaForm, out S: ProblemSource> {
    val source: S
    val form: F
    fun toFileName(): String
}
