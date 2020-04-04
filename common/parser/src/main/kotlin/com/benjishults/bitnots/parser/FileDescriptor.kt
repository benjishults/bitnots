package com.benjishults.bitnots.parser

import com.benjishults.bitnots.theory.formula.FormulaForm

interface FileDescriptor {
    val form: FormulaForm
    fun toFileName(): String
}
