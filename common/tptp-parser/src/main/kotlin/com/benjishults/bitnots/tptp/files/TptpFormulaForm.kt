package com.benjishults.bitnots.tptp.files

enum class TptpFormulaForm(val form: Char) {
    CNF('-'),
    FOF('+'),
    TFF('_'),
    TFF_WITH_ARITHMETIC('='),
    THF('^');

    companion object {
        fun findByForm(form: Char) = values().find { it.form == form } ?: error("Malformed formula form: '$form'.")
    }

}
