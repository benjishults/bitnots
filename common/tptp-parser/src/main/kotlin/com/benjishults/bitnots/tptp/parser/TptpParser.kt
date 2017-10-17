package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.terms.BV
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.Const
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.parser.Parser
import com.benjishults.bitnots.parser.Tokenizer
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.CnfAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRoles
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import java.io.BufferedReader
import kotlin.reflect.KFunction

data class Functor(val cons: String, val args: List<Functor>) {
    fun toFormula(bvs: Set<BoundVariable>): Formula<*> =
            if (args.isEmpty())
                Prop(cons)/*, Atomic*/
            else
                args.map { it.toTerm(bvs) }.toList().let {
                    Pred(cons, it.size)(it)/*, Atomic*/
                }


    fun toTerm(bvs: Set<BoundVariable>): Term<*> =
            if (args.isEmpty()) {
                if (cons.first().isUpperCase()) {
                    if (cons in bvs.map { it.cons.name }) {
                        BV(cons)
                    } else {
                        FV(cons)
                    }
                } else
                    Const(cons)
            } else
                args.map { it.toTerm(bvs) }.toList().let {
                    Fn(cons, it.size)(it)
                }

    companion object {
        fun parse(tokenizer: TptpTokenizer): Functor =
                tokenizer.popToken().let { functor ->
                    functor.toIntOrNull()?.let {
                        Functor(functor, emptyList())
                    } ?: run {
                        require(functor.first().isLetter()) {
                            tokenizer.finishMessage("Expected functor when parsing '$functor'")
                        }
                        Functor(functor, generateSequence(tokenizer.peek().let {
                            if (it == "(") {
                                tokenizer.popToken()
                                parse(tokenizer)
                            } else
                                null
                        }) {
                            tokenizer.popToken().let {
                                if (it == ",") {
                                    parse(tokenizer)
                                } else if (it == ")") {
                                    null
                                } else
                                    error(tokenizer.finishMessage("Expecting ',' or '), but found '$it'"))
                            }
                        }.toList())
                    }
                }
    }
}

interface InnerParser<out T> {
    companion object {
        val keywords = arrayOf("fof", "cnf", "thf", "tff", "include")
        val punctuation = arrayOf("(", ")", ",", ".", "[", "]", ":")
        val unitaryFormulaInitial = arrayOf("?", "!", "~", "(")
    }

    fun parse(tokenizer: TptpTokenizer, parser: AbstractTptpParser<*>): T
}

class TptpFile(val inputs: List<AnnotatedFormula>) {

    companion object : InnerParser<TptpFile> {
        override fun parse(tokenizer: TptpTokenizer, parser: AbstractTptpParser<*>): TptpFile {
            return TptpFile(
                    mutableListOf<AnnotatedFormula>().apply {
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
                                    "include" -> addAll(Include.parse(tokenizer, parser))
//                                    "cnf", "fof" -> add(par(tokenizer))
                                    "fof", "cnf" -> add(parser.parseAnnotatedFormula(tokenizer))
                                    else -> error(tokenizer.finishMessage("Parsing for '${it}' not yet implemented"))
                                }
                            }
                        }
                    })
        }
    }

}

data class Include(val axioms: List<AnnotatedFormula>) {
    companion object : InnerParser<List<AnnotatedFormula>> {
        override fun parse(tokenizer: TptpTokenizer, parser: AbstractTptpParser<*>): List<AnnotatedFormula> {
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
                ).inputs.run {
                    tokenizer.skipToEndParen()
                    TptpTokenizer.ensure(".", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
                    this
                }
            }
        }
    }
}

abstract class AbstractTptpParser<F> : Parser<TptpFile, TptpTokenizer, F> {
    
    abstract val formulaType: String
    
    abstract val annotatedFormulaFactory: KFunction<AnnotatedFormula>

    override fun parseAnnotatedFormula(tokenizer: TptpTokenizer): AnnotatedFormula {
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
                FormulaRoles.valueOf(tokenizer.popToken()).also {
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
