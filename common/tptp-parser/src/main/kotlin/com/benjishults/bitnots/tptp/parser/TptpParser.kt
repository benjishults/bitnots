package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.inference.rules.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.Const
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.Term
import java.nio.file.Path

const val UNEXPECTED_END_OF_INPUT = "Unexpected end of input."

interface InnerParser<out T> {
    companion object {
        val keywords = arrayOf("fof", "cnf", "thf", "tff", "include")
        val punctuation = arrayOf("(", ")", ",", ".", "[", "]", ":")
        val operators = arrayOf("!", "?", "~", "&", "|", "<=>", "=>", "<=", "<->", "~|", "~&", "*", "+")
        val binaryConnective = arrayOf("<=>", "=>", "<=", "<->", "~|", "~&")
        val predicates = arrayOf("!=", "\$true", "\$false")
    }

    fun parse(tokenizer: TptpTokenizer): T
}

interface FofInnerParser<out T> : InnerParser<T> {

    override fun parse(tokenizer: TptpTokenizer) = parse(tokenizer, emptySet())

    fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): T
}

class TptpFile(val inputs: List<TptpInput>) {

    companion object : InnerParser<TptpFile> {
        override fun parse(tokenizer: TptpTokenizer): TptpFile {
            return TptpFile(generateSequence {
                try {
                    tokenizer.peekKeyword()
                } catch (e: Exception) {
                    if (e.message == UNEXPECTED_END_OF_INPUT)
                        null
                    else
                        throw e
                }?.let {
                    when (it) {
                        "cnf" -> CnfAnnotatedFormula.parse(tokenizer)
                        "fof" -> FofAnnotatedFormula.parse(tokenizer)
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

sealed class AnnotatedFormula : TptpInput()

class Include : TptpInput()

data class CnfAnnotatedFormula(val name: String, val formulaRole: String, val clause: List<SimpleSignedFormula<*>>) : AnnotatedFormula() {

    companion object : InnerParser<CnfAnnotatedFormula> {
        override fun parse(tokenizer: TptpTokenizer): CnfAnnotatedFormula {
            TptpTokenizer.ensure("cnf", tokenizer.popToken())
            TptpTokenizer.ensure("(", tokenizer.popToken())
            return CnfAnnotatedFormula(
                    tokenizer.popToken().also {
                        TptpTokenizer.ensure(",", tokenizer.popToken())
                    },
                    tokenizer.popToken().also {
                        TptpTokenizer.ensure(",", tokenizer.popToken())
                    },
                    Clause.parse(tokenizer).also {
                        when (tokenizer.popToken()) {
                            "," -> tokenizer.moveToEndParen()
                            ")" -> TptpTokenizer.ensure(".", tokenizer.popToken())
                        }
                    })
        }
    }

}

data class FofAnnotatedFormula(val name: String, val formulaRole: String, val formula: Formula<*>) : AnnotatedFormula() {

    companion object : InnerParser<FofAnnotatedFormula> {
        override fun parse(tokenizer: TptpTokenizer): FofAnnotatedFormula {
            TptpTokenizer.ensure("cnf", tokenizer.popToken())
            TptpTokenizer.ensure("(", tokenizer.popToken())
            return FofAnnotatedFormula(
                    tokenizer.popToken().also {
                        TptpTokenizer.ensure(",", tokenizer.popToken())
                    },
                    tokenizer.popToken().also {
                        TptpTokenizer.ensure(",", tokenizer.popToken())
                    },
                    TptpFofFof.parse(tokenizer).also {
                        when (tokenizer.popToken()) {
                            "," -> tokenizer.moveToEndParen()
                            ")" -> TptpTokenizer.ensure(".", tokenizer.popToken())
                        }
                    })
        }
    }

}



fun <V> parse(tokenizer: TptpTokenizer, upperFactory: (String) -> V, closedFactory: (String) -> V, argsFactory: (String, Int, List<Term<*>>) -> V): V {
    // function(lower), constant (lower), or variable (upper)
    return tokenizer.popToken().let { name ->
        name.first().let { first ->
            if (first.isUpperCase()) {
                upperFactory(name)
            } else if (first.isLowerCase()) {
                tokenizer.peek().let { next ->
                    when (next) {
                        "(" -> {
                            tokenizer.popToken()
                            tokenizer.peek().let {
                                if (it == ")") {
                                    tokenizer.popToken()
                                    closedFactory(name)
                                } else {
                                    generateSequence(TptpFofTerm.parse(tokenizer)) {
                                        tokenizer.popToken().let {
                                            when (it) {
                                                ")" -> null
                                                "," -> TptpFofTerm.parse(tokenizer)
                                                else -> error("Expected punctuation no '$it'.") // TptpFofTerm.parse(tokenizer)
                                            }
                                        }
                                    }.asIterable().toList().let {
                                        argsFactory(name, it.size, it)
                                    }
                                }
                            }
                        }
                        in InnerParser.punctuation -> closedFactory(name)
                        else -> error("Unexpected '$next'.")
                    }
                }
            } else {
                error("Unexpected '$name'.")
            }
        }
    }
}

object TptpFofTerm : InnerParser<Term<*>> {
    override fun parse(tokenizer: TptpTokenizer): Term<*> =
            parse(tokenizer, { FV(it) }, { Const(it) }, { name, arity, args -> Fn(name, arity)(args) })
}

object TptpCnfFof : InnerParser<Formula<*>> {
    override fun parse(tokenizer: TptpTokenizer): Formula<*> =
            parse(tokenizer, { Prop(it) }, { Prop(it) }, { name, arity, args -> Pred(name, arity)(args) })
}

/**
 * NOTE: In TPTP, function symbols and predicate symbols should be disjoint in order to avoid parsing trouble.
 */
object TptpParser {
    fun parseFile(file: Path): TptpFile {
        file.toFile().reader().buffered().use {
            return TptpFile.parse(TptpTokenizer(it))
        }
    }
}
