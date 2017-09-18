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
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.TptpProperties

const val UNEXPECTED_END_OF_INPUT = "Unexpected end of input."

interface InnerParser<out T> {
    companion object {
        val keywords = arrayOf("fof", "cnf", "thf", "tff", "include")
        val punctuation = arrayOf("(", ")", ",", ".", "[", "]", ":")
        val operators = arrayOf("!", "?", "~", "&", "|", "<=>", "=>", "<=", "<->", "~|", "~&", "*", "+")
        val binaryConnective = arrayOf("<=>", "=>", "<=", "<->", "~|", "~&")
        val unitaryFormulaInitial = arrayOf("?", "!", "~", "(")
        val predicates = arrayOf("!=", "\$true", "\$false")
    }

    fun parse(tokenizer: TptpTokenizer): T
}

interface FofInnerParser<out T> : InnerParser<T> {

    override fun parse(tokenizer: TptpTokenizer) = parse(tokenizer, emptySet())

    fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): T
}

class TptpFile(val inputs: List<AnnotatedFormula>) {

    companion object : InnerParser<TptpFile> {
        override fun parse(tokenizer: TptpTokenizer): TptpFile {
            return TptpFile(
                    mutableListOf<AnnotatedFormula>().apply {
                        while (true) {
                            try {
                                tokenizer.peekKeyword()
                            } catch (e: Exception) {
                                if (e.message == tokenizer.finishMessage(UNEXPECTED_END_OF_INPUT))
                                    break
                                else
                                    throw e
                            }.let {
                                when (it) {
                                    "cnf" -> add(CnfAnnotatedFormula.parse(tokenizer))
                                    "fof" -> add(FofAnnotatedFormula.parse(tokenizer))
                                    "include" -> addAll(Include.parse(tokenizer))
                                    else -> error(tokenizer.finishMessage("Parsing for '${it}' not yet implemented"))
                                }
                            }
                        }
                    })
        }
    }

}

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

sealed class AnnotatedFormula(open val name: String = "")

data class Include(val axioms: List<AnnotatedFormula>) {
    companion object : InnerParser<List<AnnotatedFormula>> {
        override fun parse(tokenizer: TptpTokenizer): List<AnnotatedFormula> {
            TptpTokenizer.ensure("include", tokenizer.popToken())
            TptpTokenizer.ensure("(", tokenizer.popToken())

            return tokenizer.popToken().substring("Axioms/".length).let {
                TptpFile.parse(TptpTokenizer(
                        TptpFileFetcher.findAxiomsFile(
                                TptpDomain.valueOf(it.substring(0, 3)),
                                TptpFormulaForm.findByForm(it.substring(6, 7).first()),
                                Integer.parseInt(it.substring(3, 6), 10),
                                Integer.valueOf(it.substring(7, it.indexOf('.'))))
                                .toFile()
                                .reader().buffered(), it)
                ).inputs.run {
                    tokenizer.parseCommaSeparatedListOfStringsToEndParen().takeIf {
                        TptpTokenizer.ensure(".", tokenizer.popToken())
                        it.isNotEmpty()
                    }?.let { include ->
                        filter {
                            it.name in include
                        }.toList()
                    } ?: this
                }
            }
        }
    }
}

data class CnfAnnotatedFormula(override val name: String, val formulaRole: FormulaRoles, val clause: List<SimpleSignedFormula<*>>) : AnnotatedFormula() {

    companion object : InnerParser<CnfAnnotatedFormula> {
        override fun parse(tokenizer: TptpTokenizer): CnfAnnotatedFormula {
            TptpTokenizer.ensure("cnf", tokenizer.popToken())
            TptpTokenizer.ensure("(", tokenizer.popToken())
            return CnfAnnotatedFormula(
                    tokenizer.popToken().also {
                        TptpTokenizer.ensure(",", tokenizer.popToken())
                    },
                    FormulaRoles.valueOf(tokenizer.popToken()).also {
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

data class FofAnnotatedFormula(override val name: String, val formulaRole: FormulaRoles, val formula: Formula<*>) : AnnotatedFormula() {

    companion object : InnerParser<FofAnnotatedFormula> {
        override fun parse(tokenizer: TptpTokenizer): FofAnnotatedFormula {
            TptpTokenizer.ensure("fof", tokenizer.popToken())
            TptpTokenizer.ensure("(", tokenizer.popToken())
            return FofAnnotatedFormula(
                    tokenizer.popToken().also {
                        TptpTokenizer.ensure(",", tokenizer.popToken())
                    },
                    FormulaRoles.valueOf(tokenizer.popToken()).also {
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
                                                else -> error(tokenizer.finishMessage("Expected punctuation no '$it'")) // TptpFofTerm.parse(tokenizer)
                                            }
                                        }
                                    }.asIterable().toList().let {
                                        argsFactory(name, it.size, it)
                                    }
                                }
                            }
                        }
                        in InnerParser.punctuation -> closedFactory(name)
                        else -> error(tokenizer.finishMessage("Unexpected '$next'"))
                    }
                }
            } else {
                error(tokenizer.finishMessage("Unexpected '$name'"))
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
            return TptpFile.parse(TptpTokenizer(it, file.toString()))
        }
    }
}
