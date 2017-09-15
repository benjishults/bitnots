package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import com.benjishults.bitnots.model.formulas.fol.equality.Equals
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.terms.BV
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.Const
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.Term

enum class AssociativeLogicalBinaryConnector(val bin: Char) {
    SPACE(' '),
    AND('&'),
    OR('|');

    companion object {
        @JvmStatic
        fun fromChar(bin: Char): AssociativeLogicalBinaryConnector = values().find { it.bin == bin } ?: error("No binary connector for '$bin'.")
    }
}

object TptpFofFof : FofInnerParser<Formula<*>> {
    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Formula<*> =
            tokenizer.peek().let {
                if (it == "[")
                    error("Sequents not yet supported.")
                else
                    TptpLogicFof.parse(tokenizer, bvs)
            }
}

object TptpLogicFof : FofInnerParser<Formula<*>> {
    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Formula<*> =
            // TODO this is kind of ugly. refactor
            tokenizer.peek().let {
                if (it.first().isLetter()) {
                    generateSequence(TptpUnitaryFof.parse(tokenizer, bvs) to AssociativeLogicalBinaryConnector.SPACE) { (_, bin) ->
                        tokenizer.peek().let {
                            if (it.first() == bin.bin) {
                                tokenizer.popToken().run {
                                    TptpUnitaryFof.parse(tokenizer, bvs) to bin
                                }
                            } else if (bin == AssociativeLogicalBinaryConnector.SPACE) {
                                tokenizer.popToken().run {
                                    TptpUnitaryFof.parse(tokenizer, bvs) to AssociativeLogicalBinaryConnector.fromChar(it.first())
                                }
                            } else error("Encountered '$it' while looking for '$bin'.")
                        }
                    }.toList().run {
                        when (last().second) {
                            AssociativeLogicalBinaryConnector.SPACE -> first().first
                            AssociativeLogicalBinaryConnector.OR -> Or(*this.map { it.first }.toTypedArray())
                            AssociativeLogicalBinaryConnector.AND -> And(*this.map { it.first }.toTypedArray())
                        }
                    }
                } else {
                    when (it) {
                        "?", "!" -> QuantifiedFormula.parse(tokenizer, bvs)
                        "~" -> TptpFofNotParser.parse(tokenizer, bvs)
                        else -> error("Unexpected character at beginning of logic FOF: '${it}'.")
                    }
                }
            }
}

data class Functor(val cons: String, val args: List<Functor>) {
    fun toFormula(bvs: Set<BoundVariable>): Formula<*> =
            if (args.isEmpty())
                Prop(cons)
            else
                args.map { it.toTerm(bvs) }.toList().let {
                    Pred(cons, it.size)(it)
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
                    require(functor.first().isLetter()) { "Expected functor when parsing '$functor'." }
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
                                error("Expecting ',' or '), but found '$it'.")
                        }
                    }.toList())
                }
    }
}

object TptpUnitaryFof : FofInnerParser<Formula<*>> {
    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Formula<*> =
            tokenizer.peek().let {
                if (it.first().isLetter()) {
                    // we are either facing a predicate (or propositional variable) or an inequality
                    // to tell the difference, we need to look past this next functor and see if we hit "!=" or something else
                    // so, we just count parens until we get to "!=", or something unexpected (like EOF even)
                    // If we hit !=, then we treat the foregoing as a term, otherwise, a predicate.
                    Functor.parse(tokenizer).let { first ->
                        tokenizer.peek().let {
                            if (it == "!=") {
                                tokenizer.popToken()
                                Not(Equals(first.toTerm(bvs), Functor.parse(tokenizer).toTerm(bvs)))
                            } else
                                first.toFormula(bvs)
                        }
                    }
                } else {
                    when (it) {
                        "?", "!" -> QuantifiedFormula.parse(tokenizer, bvs)
                        "~" -> TptpFofNotParser.parse(tokenizer, bvs)
                        "[" -> error("Sequents not yet supported.")
                        "(" -> TptpLogicFof.parse(tokenizer, bvs).also { TptpTokenizer.ensure(")", tokenizer.popToken()) }
                        else -> error("Unexpected character at beginning of FOF '${it}'.")
                    }
                }
            }
}

object TptpFofNotParser : FofInnerParser<Not> {
    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Not {
        TptpTokenizer.ensure("~", tokenizer.popToken())
        return Not(TptpUnitaryFof.parse(tokenizer, bvs))
    }
}

object QuantifiedFormula : FofInnerParser<VarBindingFormula> {
    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): VarBindingFormula =
            tokenizer.popToken().let {
                when (it) {
                    "!" -> {
                        parseBoundVars(tokenizer).let {
                            ForAll(TptpUnitaryFof.parse(tokenizer, bvs + it), *it.toTypedArray())
                        }
                    }
                    "?" -> {
                        parseBoundVars(tokenizer).let {
                            ForSome(TptpUnitaryFof.parse(tokenizer, bvs + it), *it.toTypedArray())
                        }
                    }
                    else -> error("Expecting '!' or '?', got '$it'.")
                }
            }

    private fun parseBoundVars(tokenizer: TptpTokenizer): Set<BoundVariable> {
        TptpTokenizer.ensure("[", tokenizer.popToken())
        return generateSequence(parseBoundVar(tokenizer)) {
            tokenizer.popToken().let {
                when (it) {
                    "," -> parseBoundVar(tokenizer)
                    "]" -> null
                    else -> error("Expected ',' or ,], but got '$it'.")
                }
            }
        }.toSet()
    }

    private fun parseBoundVar(tokenizer: TptpTokenizer): BoundVariable =
            tokenizer.popToken().let {
                if (it.first().isUpperCase()) {
                    BV(it)
                } else
                    error("Expected upper-case word but got '$it'.")
            }
}

//object TptpFofLogicParser : FofInnerParser<Formula<*>> {
//    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Formula<*> =
//            TptpFofFof.parse(tokenizer, bvs).let {
//                first ->
//                tokenizer.peek().let {
//                    when (it) {
//                        ")" -> first
//                        "<=>" -> tokenizer.popToken().run {
//                            Iff(first, TptpFofFof.parse(tokenizer, bvs))
//                        }
//                        "=>" -> tokenizer.popToken().run {
//                            Implies(first, TptpFofFof.parse(tokenizer, bvs))
//                        }
//                        "<=" -> tokenizer.popToken().run {
//                            Implies(TptpFofFof.parse(tokenizer, bvs), first)
//                        }
//                        "<~>" -> tokenizer.popToken().run {
//                            Not(Iff(first, TptpFofFof.parse(tokenizer, bvs)))
//                        }
//                        "~|" -> tokenizer.popToken().run {
//                            Not(Or(first, TptpFofFof.parse(tokenizer, bvs)))
//                        }
//                        "~&" -> tokenizer.popToken().run {
//                            Not(And(first, TptpFofFof.parse(tokenizer, bvs)))
//                        }
//                        else -> error("Unexpected token: '$it'.")
//                    }
//                }
//            }
//}


