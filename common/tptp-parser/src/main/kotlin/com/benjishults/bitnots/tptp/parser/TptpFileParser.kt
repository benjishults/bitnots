package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.parser.Tokenizer
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaForm

class TptpFileParser<FF: FormulaForm> : InnerParser<FF> {

    private val includeParser = IncludeParser<FF>()

    override fun <AF: AnnotatedFormula> parse(tokenizer: TptpTokenizer, parser: AbstractTptpParser<FF, AF>): List<AF> {
        return mutableListOf<AF>().apply {
            while (true) {
                try {
                    tokenizer.peekKeyword()
                } catch (e: Exception) {
                    if (e.message == tokenizer.finishMessage(Tokenizer.UNEXPECTED_END_OF_INPUT))
                        break
                    else
                        throw e
                }.let {
                    when (it) {
                        "include" -> addAll(includeParser.parse(tokenizer, parser))
                        //                                    "cnf", "fof" -> add(par(tokenizer))
                        "fof", "cnf" -> add(parser.parseAnnotatedFormula(tokenizer))
                        else -> error(tokenizer.finishMessage("Parsing for '${it}' not yet implemented"))
                    }
                }
            }
        }
    }
}
