package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.concrete.NegativePredicate
import com.benjishults.bitnots.inference.rules.concrete.NegativePropositionalVariable
import com.benjishults.bitnots.inference.rules.concrete.PositivePredicate
import com.benjishults.bitnots.inference.rules.concrete.PositivePropositionalVariable
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.model.terms.Const
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.Term
import java.nio.file.Path

val UNEXPECTED_END_OF_INPUT = "Unexpected end of input."

interface InnerParser<out T> {
    companion object {
        val keywords = arrayOf("fof", "cnf", "thf", "tff", "include")
        val punctuation = arrayOf("(", ")", ",", ".", "[", "]", ":")
        val operators = arrayOf("!", "?", "~", "&", "|", "<=>", "=>", "<=", "<->", "~|", "~&", "*", "+")
        val predicates = arrayOf("!=", "\$true", "\$false")
    }

    fun parse(tokenizer: TptpTokenizer): T
//    fun parse(tokenizer: TptpTokenizer, firstToken: String): T {
//        error("Unexpected call to parse(TptpTokenizer, String)" }
//    }
}

class TptpFile(val inputs: List<TptpInput>) {

    companion object : InnerParser<TptpFile> {
        override fun parse(tokenizer: TptpTokenizer): TptpFile {
            return TptpFile(generateSequence {
                try {
                    tokenizer.parseKeyword()
                } catch (e: Exception) {
                    null
                }?.let {
                    when (it) {
                        "cnf" -> CnfAnnotatedFormula.parse(tokenizer.backup())
                        else -> error("Parsing for '${it}' not yet implemented.")
                    }
                }
            }.asIterable().toList())
        }
    }

}

sealed class TptpInput

enum class FormulaRoles {
    axiom,
    hypothesis,
    definition,
    assumption,
    lemma,
    theorem,
    corollary,
    conjecture,
    negated_conjecture,
    plain,
    type,
    fi_domain,
    fi_functors,
    fi_predicates,
    unknown
}

sealed class AnnotatedFormula : TptpInput() {
}

class Include : TptpInput() {

}

data class CnfAnnotatedFormula(val name: String, val formulaRole: String, val formula: Clause) : AnnotatedFormula() {

    companion object : InnerParser<CnfAnnotatedFormula> {
        override fun parse(tokenizer: TptpTokenizer): CnfAnnotatedFormula {
            TptpTokenizer.ensure("cnf", tokenizer.nextToken())
            TptpTokenizer.ensure("(", tokenizer.nextToken())
            return CnfAnnotatedFormula(
                    tokenizer.nextToken().also {
                        TptpTokenizer.ensure(",", tokenizer.nextToken())
                    },
                    tokenizer.nextToken().also {
                        TptpTokenizer.ensure(",", tokenizer.nextToken())
                    },
                    Clause.parse(tokenizer).also {
                        when (tokenizer.nextToken()) {
                            "," -> tokenizer.moveToEndParen()
                            ")" -> {
                                // continue
                            }
                        }
                    })
        }
    }

}

class Clause(val literals: List<SignedFormula<*>>) {

    companion object : InnerParser<Clause> {
        override fun parse(tokenizer: TptpTokenizer): Clause {
            return Clause(
                    tokenizer.nextToken().let {
                        if (it == "(") {
                            Disjunct.parse(tokenizer).also { TptpTokenizer.ensure(")", tokenizer.nextToken()) }
                        } else {
                            Disjunct.parse(tokenizer.backup())
                        }
                    })
        }

    }
}

class Disjunct {

    companion object : InnerParser<List<SignedFormula<*>>> {
        override fun parse(tokenizer: TptpTokenizer): List<SignedFormula<*>> {
            return generateSequence(Literal.parse(tokenizer)) {
                tokenizer.nextToken().let {
                    when (it) {
                        "|" -> Literal.parse(tokenizer)
                        in InnerParser.punctuation -> {
                            tokenizer.backup()
                            null
                        }
                        else -> error("Unexpected token: '$it'.")
                    }
                }
            }.asIterable().toList()
        }
    }
}

class Literal {

    companion object : InnerParser<SignedFormula<*>> {

        override fun parse(tokenizer: TptpTokenizer): SignedFormula<*> =
                if (tokenizer.nextToken() == "~") {
                    TptpFofFof.parse(tokenizer).let {
                        when (it) {
                            is PropositionalVariable -> NegativePropositionalVariable(it)
                            is Predicate -> NegativePredicate(it)
                            else -> error("Unexpected type of formula '${it::class.simpleName}'.")
                        }
                    }
                } else {
                    TptpFofFof.parse(tokenizer.backup()).let {
                        when (it) {
                            is PropositionalVariable -> PositivePropositionalVariable(it)
                            is Predicate -> PositivePredicate(it)
                            else -> error("Unexpected type of formula '${it::class.simpleName}'.")
                        }
                    }
                }
    }

}

fun <V> parse(tokenizer: TptpTokenizer, upperFactory: (String) -> V, closedFactory: (String) -> V, argsFactory: (String, Int, List<Term<*>>) -> V): V {
    // function(lower), constant (lower), or variable (upper)
    return tokenizer.nextToken().let { name ->
        name.first().let { first ->
            if (first.isUpperCase()) {
                upperFactory(name)
            } else if (first.isLowerCase()) {
                tokenizer.nextToken().let { next ->
                    when (next) {
                        "(" -> {
                            tokenizer.nextToken().let {
                                if (it == ")") {
                                    closedFactory(name)
                                } else {
                                    generateSequence(parse(tokenizer.backup(), { FV(it) }, { Const(it) }, { name, arity, args -> Fn(name, arity)(args) })) {
                                        tokenizer.nextToken().let {
                                            when (it) {
                                                ")" -> null
                                                "," -> parse(tokenizer, { FV(it) }, { Const(it) }, { name, arity, args -> Fn(name, arity)(args) })
                                                else -> parse(tokenizer.backup(), { FV(it) }, { Const(it) }, { name, arity, args -> Fn(name, arity)(args) })
                                            }
                                        }
                                    }.asIterable().toList().let {
                                        argsFactory(name, it.size, it)
                                    }
                                }
                            }
                        }
                        in InnerParser.punctuation -> closedFactory(name).also {
                            tokenizer.backup()
                        }
                        else -> error("Unexpected '$next'.")
                    }
                }
            } else {
                error("Unexpected '$name'.")
            }
        }
    }
}

class TptpFofTerm {
    companion object : InnerParser<Term<*>> {
        override fun parse(tokenizer: TptpTokenizer): Term<*> =
                parse(tokenizer, { FV(it) }, { Const(it) }, { name, arity, args -> Fn(name, arity)(args) })
    }
}

class TptpFofFof {
    companion object : InnerParser<Formula<*>> {
        override fun parse(tokenizer: TptpTokenizer): Formula<*> =
                parse(tokenizer, { Prop(it) }, { Prop(it) }, { name, arity, args -> Pred(name, arity)(args) })
    }
}

/**
 * NOTE: In TPTP, function symbols and predicate symbols should be disjoint in order to avoid parsing trouble.
 */
class TptpParser {
    companion object {
        fun parseFile(file: Path): TptpFile {
            file.toFile().reader().buffered().use {
                return TptpFile.parse(TptpTokenizer(it))
            }
        }
    }
}
