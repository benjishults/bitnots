package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.inference.rules.concrete.NegativePredicate
import com.benjishults.bitnots.inference.rules.concrete.NegativePropositionalVariable
import com.benjishults.bitnots.inference.rules.concrete.PositivePredicate
import com.benjishults.bitnots.inference.rules.concrete.PositivePropositionalVariable
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.parser.Tokenizer
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.CnfAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRoles
import com.benjishults.bitnots.theory.formula.SimpleSignedFormula

object TptpCnfParser : AbstractTptpParser<List<SimpleSignedFormula<*>>>() {
    override val formulaType = "cnf"
    override val annotatedFormulaFactory= CnfAnnotatedFormula::class.constructors.first()

    // TODO abstract thiscamel-spring-javaconfig
    override fun parse(tokenizer: TptpTokenizer): TptpFile {
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
                                "cnf" -> add(parseAnnotatedFormula(tokenizer))
                                "include" -> addAll(Include.parse(tokenizer, TptpFofParser))
                                else -> error(tokenizer.finishMessage("Parsing for '${it}' not yet implemented"))
                            }
                        }
                    }
                })
    }

    override fun parseFormula(tokenizer: TptpTokenizer): List<SimpleSignedFormula<*>> =
            parseClause(tokenizer)

    fun parseClause(tokenizer: TptpTokenizer): List<SimpleSignedFormula<*>> =
            tokenizer.peek().let {
                if (it == "(") {
                    tokenizer.popToken()
                    parseDisjunct(tokenizer).also { TptpTokenizer.ensure(")", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) } }
                } else {
                    parseDisjunct(tokenizer)
                }
            }

    fun parseDisjunct(tokenizer: TptpTokenizer): List<SimpleSignedFormula<*>> {
        return generateSequence(parseLiteral(tokenizer)) {
            tokenizer.peek().let {
                when (it) {
                    "|" -> {
                        tokenizer.popToken()
                        parseLiteral(tokenizer)
                    }
                    in InnerParser.punctuation -> {
                        null
                    }
                    else -> error(tokenizer.finishMessage("Unexpected token: '$it'"))
                }
            }
        }.asIterable().toList()
    }

    fun parseLiteral(tokenizer: TptpTokenizer): SimpleSignedFormula<*> =
            if (tokenizer.peek() == "~") {
                tokenizer.popToken()
                Functor.parse(tokenizer).toFormula(emptySet()).let {
                    when (it) {
                        is PropositionalVariable -> NegativePropositionalVariable(it)
                        is Predicate -> NegativePredicate(it)
                        else -> error(tokenizer.finishMessage("Unexpected type of formula '${it::class.simpleName}'"))
                    }
                }
            } else {
                Functor.parse(tokenizer).toFormula(emptySet()).let {
                    when (it) {
                        is PropositionalVariable -> PositivePropositionalVariable(it)
                        is Predicate -> PositivePredicate(it)
                        else -> error(tokenizer.finishMessage("Unexpected type of formula '${it::class.simpleName}'"))
                    }
                }
            }


}
