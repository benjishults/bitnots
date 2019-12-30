package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm

class IncludeParser<AF : AnnotatedFormula> : InnerParser<AF> {
    override fun parse(tokenizer: TptpTokenizer, parser: AbstractTptpParser<AF, *>): List<AF> {
        TptpTokenizer.ensure("include", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
        TptpTokenizer.ensure("(", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }

        return tokenizer.popToken().substring("Axioms/".length).let {
            parser.parse(parser.tokenizerFactory(
                    TptpFileFetcher.findAxiomsFile(
                            TptpDomain.valueOf(it.substring(0, 3)),
                            TptpFormulaForm.findByForm(it.substring(6, 7).first()),
                            Integer.parseInt(it.substring(3, 6), 10),
                            Integer.valueOf(it.substring(7, it.indexOf('.'))))
                            .toFile()
                            .reader().buffered(), it)
            ).also {
                tokenizer.skipToEndParen()
                TptpTokenizer.ensure(".", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
            }
        }
    }
}
