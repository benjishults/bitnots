package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.theory.formula.AnnotatedFormula

interface InnerParser<T : AnnotatedFormula> {
    companion object {
        val keywords = arrayOf("fof", "cnf", "thf", "tff", "include")
        val punctuation = arrayOf("(", ")", ",", ".", "[", "]", ":")
        val unitaryFormulaInitial = arrayOf("?", "!", "~", "(")
    }

    fun parse(tokenizer: TptpTokenizer, parser: AbstractTptpParser<T, *>): List<T>
}
