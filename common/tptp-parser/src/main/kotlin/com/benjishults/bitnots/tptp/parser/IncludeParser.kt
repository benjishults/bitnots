package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaForm
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm

class IncludeParser<FF : FormulaForm> : InnerParser<FF> {
    override fun <AF : AnnotatedFormula> parse(tokenizer: TptpTokenizer, parser: AbstractTptpParser<FF, AF>): List<AF> {
        TptpTokenizer.ensure("include", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
        TptpTokenizer.ensure("(", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }

        val path = tokenizer.popToken()
        val file =
            if (path.startsWith("Axioms/SET007/"))
                path.substring(14)
            else
                path.substring(7)
        return parser.parse(
            parser.tokenizerFactory(
                TptpFileFetcher.findAxiomsFile(
                    TptpDomain.valueOf(file.substring(0, 3)),
                    TptpFormulaForm.findByRepresentation(file.substring(6, 7).first()),
                    file.substring(3, 6).toLong(10),
                    Integer.valueOf(file.substring(7, file.indexOf('.')))
                ).toFile()
                    .reader()
                    .buffered(),
                file
            )
        ).also {
            // println("WARN: including axioms is not implemented")
            tokenizer.skipToEndParen()
            TptpTokenizer.ensure(".", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
            // emptyList()
        }
    }

}
