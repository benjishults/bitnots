package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.util.isAtomic
import com.benjishults.bitnots.model.formulas.util.isLiteral
import com.benjishults.bitnots.parser.Tokenizer
import com.benjishults.bitnots.theory.formula.CNF
import com.benjishults.bitnots.theory.formula.CnfAnnotatedFormula

object TptpCnfParser : AbstractTptpParser<CNF, CnfAnnotatedFormula>() {
    override val formulaType = "cnf"
    override val annotatedFormulaFactory = CnfAnnotatedFormula::class.constructors.first()
    private val includeParser = IncludeParser<CNF>()

    // TODO abstract this
    override fun parse(tokenizer: TptpTokenizer): List<CnfAnnotatedFormula> {
        return mutableListOf<CnfAnnotatedFormula>().apply {
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
                        "cnf"     -> add(parseAnnotatedFormula(tokenizer))
                        "include" -> addAll(includeParser.parse(tokenizer, TptpCnfParser))
                        else      -> error(tokenizer.finishMessage("Parsing for '${it}' not yet implemented"))
                    }
                }
            }
        }
    }

    override fun parseFormula(tokenizer: TptpTokenizer): Formula =
            parseClause(tokenizer)

    fun parseClause(tokenizer: TptpTokenizer): Formula =
            tokenizer.peek().let {
                if (it == "(") {
                    tokenizer.popToken()
                    parseDisjunct(tokenizer).also {
                        TptpTokenizer.ensure(")", tokenizer.popToken())?.let {
                            error(tokenizer.finishMessage(it))
                        }
                    }
                } else {
                    parseDisjunct(tokenizer)
                }
            }

    fun parseDisjunct(tokenizer: TptpTokenizer): Formula {
        return generateSequence(parseLiteral(tokenizer)) {
            tokenizer.peek().let {
                when (it) {
                    "|"                        -> {
                        tokenizer.popToken()
                        parseLiteral(tokenizer)
                    }
                    in InnerParser.punctuation -> null
                    else                       -> error(tokenizer.finishMessage("Unexpected token: '$it'"))
                }
            }
        }.asIterable().toList().run {
            if (size > 1)
                Or(*toTypedArray())
            else
                first()
        }
    }

    fun parseLiteral(tokenizer: TptpTokenizer): Formula =
            if (tokenizer.peek() == "~") {
                tokenizer.popToken()
                Functor.parse(tokenizer).toFormula(emptySet()).let {
                    if (it.isAtomic()) {
                        Not(it)
                    } else {
                        error(tokenizer.finishMessage("Unexpected type of formula '${it::class.simpleName}'"))
                    }
                }
            } else {
                Functor.parse(tokenizer).toFormula(emptySet()).let { parsedFormula ->
                    parsedFormula.takeIf {
                        it.isLiteral()
                    } ?: error(
                            tokenizer.finishMessage("Unexpected type of formula '${parsedFormula::class.simpleName}'"))
                }
            }

}
