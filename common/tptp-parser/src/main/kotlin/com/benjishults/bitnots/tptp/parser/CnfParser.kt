package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.inference.rules.concrete.NegativePredicate
import com.benjishults.bitnots.inference.rules.concrete.NegativePropositionalVariable
import com.benjishults.bitnots.inference.rules.concrete.PositivePredicate
import com.benjishults.bitnots.inference.rules.concrete.PositivePropositionalVariable
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.theory.formula.SimpleSignedFormula

object Clause : InnerParser<List<SimpleSignedFormula<*>>> {
    override fun parse(tokenizer: TptpTokenizer): List<SimpleSignedFormula<*>> =
            tokenizer.peek().let {
                if (it == "(") {
                    tokenizer.popToken()
                    Disjunct.parse(tokenizer).also { TptpTokenizer.ensure(")", tokenizer.popToken()) }
                } else {
                    Disjunct.parse(tokenizer)
                }
            }
}

object Disjunct : InnerParser<List<SimpleSignedFormula<*>>> {
    override fun parse(tokenizer: TptpTokenizer): List<SimpleSignedFormula<*>> {
        return generateSequence(Literal.parse(tokenizer)) {
            tokenizer.peek().let {
                when (it) {
                    "|" -> {
                        tokenizer.popToken()
                        Literal.parse(tokenizer)
                    }
                    in InnerParser.punctuation -> {
                        null
                    }
                    else -> error(tokenizer.finishMessage("Unexpected token: '$it'"))
                }
            }
        }.asIterable().toList()
    }
}

object Literal : InnerParser<SimpleSignedFormula<*>> {

    override fun parse(tokenizer: TptpTokenizer): SimpleSignedFormula<*> =
            if (tokenizer.peek() == "~") {
                tokenizer.popToken()
                TptpCnfFof.parse(tokenizer).let {
                    when (it) {
                        is PropositionalVariable -> NegativePropositionalVariable(it)
                        is Predicate -> NegativePredicate(it)
                        else -> error(tokenizer.finishMessage("Unexpected type of formula '${it::class.simpleName}'"))
                    }
                }
            } else {
                TptpCnfFof.parse(tokenizer).let {
                    when (it) {
                        is PropositionalVariable -> PositivePropositionalVariable(it)
                        is Predicate -> PositivePredicate(it)
                        else -> error(tokenizer.finishMessage("Unexpected type of formula '${it::class.simpleName}'"))
                    }
                }
            }
}
