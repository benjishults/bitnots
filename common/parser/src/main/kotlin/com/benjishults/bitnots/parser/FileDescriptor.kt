package com.benjishults.bitnots.parser

interface FileDescriptor {
    val form: FormulaForm
    fun toFileName(): String
}
