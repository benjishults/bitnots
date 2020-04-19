package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaForm

interface InnerParser<FF: FormulaForm> {
    companion object {
        val keywords = arrayOf("fof", "cnf", "thf", "tff", "include")
        val punctuation = arrayOf("(", ")", ",", ".", "[", "]", ":")
        val unitaryFormulaInitial = arrayOf("?", "!", "~", "(")
    }

    fun <AF: AnnotatedFormula> parse(tokenizer: TptpTokenizer, parser: AbstractTptpParser<FF, AF>): List<AF>
}
