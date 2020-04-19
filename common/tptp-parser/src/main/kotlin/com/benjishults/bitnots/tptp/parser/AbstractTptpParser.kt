package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.parser.Parser
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaForm
import com.benjishults.bitnots.theory.formula.FormulaRole
import java.io.BufferedReader
import kotlin.reflect.KFunction

abstract class AbstractTptpParser<FF: FormulaForm, AF: AnnotatedFormula> : Parser<AF, TptpTokenizer> {

    abstract val formulaType: String

    abstract val annotatedFormulaFactory: KFunction<AF>

    override fun parseAnnotatedFormula(tokenizer: TptpTokenizer): AF {
        TptpTokenizer.ensure(formulaType, tokenizer.popToken())?.let {
            error(tokenizer.finishMessage(it))
        }
        TptpTokenizer.ensure("(", tokenizer.popToken())?.let {
            error(tokenizer.finishMessage(it))
        }
        return annotatedFormulaFactory.call(
                tokenizer.popToken().also {
                    TptpTokenizer.ensure(",", tokenizer.popToken())?.let {
                        error(tokenizer.finishMessage(it))
                    }
                },
                FormulaRole.valueOf(tokenizer.popToken()).also {
                    TptpTokenizer.ensure(",", tokenizer.popToken())?.let {
                        error(tokenizer.finishMessage(it))
                    }
                },
                parseFormula(tokenizer).also {
                    when (tokenizer.popToken()) {
                        "," -> {
                            tokenizer.moveToEndParen()
                            TptpTokenizer.ensure(".", tokenizer.popToken())?.let {
                                error(tokenizer.finishMessage(it))
                            }
                        }
                        ")" -> TptpTokenizer.ensure(".", tokenizer.popToken())?.let {
                            error(tokenizer.finishMessage(it))
                        }
                    }
                })
    }


    override val tokenizerFactory =
            { r: BufferedReader, n: String ->
                TptpTokenizer(r, n)
            }

}
