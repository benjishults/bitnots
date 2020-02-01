package com.benjishults.bitnots.tptp.files

import com.benjishults.bitnots.parser.FormulaForm

enum class TptpFormulaForm(val form: Char): FormulaForm {
    CNF('-'),
    FOF('+'),
    TFF('_'),
    TFF_WITH_ARITHMETIC('='),
    THF('^');

    companion object {
        fun findByForm(form: Char) = values().find { it.form == form } ?: error("Malformed formula form: '$form'.")
    }

}
