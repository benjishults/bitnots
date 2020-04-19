package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaForm
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm

class IncludeParser<FF: FormulaForm> : InnerParser<FF> {
    override fun <AF: AnnotatedFormula> parse(tokenizer: TptpTokenizer, parser: AbstractTptpParser<FF, AF>): List<AF> {
        TptpTokenizer.ensure("include", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
        TptpTokenizer.ensure("(", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }

        return tokenizer.popToken().substring("Axioms/".length).let {
            parser.parse(
                parser.tokenizerFactory(
                    TptpFileFetcher.findAxiomsFile(
                        TptpDomain.valueOf(it.substring(0, 3)),
                        TptpFormulaForm.findByRepresentation(it.substring(6, 7).first()),
                        it.substring(3, 6).toLong(10),
                        Integer.valueOf(it.substring(7, it.indexOf('.')))
                    ).toFile()
                        .reader()
                        .buffered(),
                    it
                )
            ).also {
                tokenizer.skipToEndParen()
                TptpTokenizer.ensure(".", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
            }
        }
    }

}
