package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import com.benjishults.bitnots.model.formulas.fol.equality.Equals
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Falsity
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.formulas.propositional.Truth
import com.benjishults.bitnots.model.terms.BV
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.Const
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.Term

data class TptpFormulaWrapper(val formula: Formula<*>, val type: TptpProduction)

sealed class TptpProduction

open class Binary : TptpProduction()
open class Unitary : TptpProduction()

object Assoc : Binary()
object NonAssoc : Binary()
object Unary : Unitary()
object Paren : Unitary()
object Quantified : Unitary()
object Atomic : Unitary()

sealed class BinaryConnector(open val connector: String) {

    sealed class NonAssociativeBinaryConnector(override val connector: String) : BinaryConnector(connector) {
        object IffConnector : NonAssociativeBinaryConnector("<=>")
        object ImpliesConnector : NonAssociativeBinaryConnector("=>")
        object ReverseImpliesConnector : NonAssociativeBinaryConnector("<=")
        object XorConnector : NonAssociativeBinaryConnector("<~>")
        object NorConnector : NonAssociativeBinaryConnector("~|")
        object NandConnector : NonAssociativeBinaryConnector("~&")
    }

    sealed class AssociativeBinaryConnector(override val connector: String) : BinaryConnector(connector) {
        object AndConnector : AssociativeBinaryConnector("&")
        object OrConnector : AssociativeBinaryConnector("|")
    }

    object NoConnector : BinaryConnector(" ")


    companion object {
        fun values() =
                BinaryConnector::class.nestedClasses.filter {
                    it.isFinal && !it.isCompanion
                }.map {
                    it.objectInstance as BinaryConnector
                }.toList() +
                        BinaryConnector::class.nestedClasses.filter {
                            it.isSealed && !it.isCompanion
                        }.flatMap {
                            it.nestedClasses
                        }.map {
                            it.objectInstance as BinaryConnector
                        }.toList()

        fun forString(connector: String) =
                values().firstOrNull {
                    it.connector == connector
                }
    }

}

object TptpFofFof : FofInnerParser<Formula<*>> {
    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Formula<*> =
            tokenizer.peek().let {
                if (it == "[")
                    error(tokenizer.finishMessage("Sequents not yet supported"))
                else
                    TptpLogicFof.parse(tokenizer, bvs).formula
            }
}

object TptpLogicFof : FofInnerParser<TptpFormulaWrapper> {
    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): TptpFormulaWrapper =
            tokenizer.peek().let {
                if (it.first().isLetter()) {
                    // this could be an inequality so, parse a functor, check the next thing, and behave appropriately
                    Functor.parse(tokenizer).let { first ->
                        tokenizer.peek().let {
                            when (it) {
                                "!=" -> {
                                    // TODO unreachable?
                                    tokenizer.popToken()
                                    parseAfterUnitaryFormula(TptpFormulaWrapper(Not(Equals(first.toTerm(bvs), Functor.parse(tokenizer).toTerm(bvs))), Unary), tokenizer, bvs)
                                }
                                "=" -> {
                                    // TODO unreachable?
                                    tokenizer.popToken()
                                    parseAfterUnitaryFormula(TptpFormulaWrapper(Equals(first.toTerm(bvs), Functor.parse(tokenizer).toTerm(bvs)), Unary), tokenizer, bvs)
                                }
                                else -> {
                                    // TODO check whether I ever use the second component of the formWrapper
                                    parseAfterUnitaryFormula(first.toFormula(bvs), tokenizer, bvs)
                                }
                            }
                        }
                    }
                } else if (it in InnerParser.unitaryFormulaInitial) {
                    parseAfterUnitaryFormula(TptpUnitaryFof.parse(tokenizer, bvs), tokenizer, bvs)
                } else if (it == "\$true") {
                    tokenizer.popToken()
                    parseAfterUnitaryFormula(TptpFormulaWrapper(Truth, Atomic), tokenizer, bvs)
                } else if (it == "\$false") {
                    tokenizer.popToken()
                    parseAfterUnitaryFormula(TptpFormulaWrapper(Falsity, Atomic), tokenizer, bvs)
                } else {
                    error(tokenizer.finishMessage("Unexpected character at beginning of logic FOF: '${it}'"))
                }
            }
}

fun parseAfterUnitaryFormula(initial: TptpFormulaWrapper, tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): TptpFormulaWrapper {
    // TODO check whether I ever use the second component of the formWrapper
    return generateSequence(initial to BinaryConnector.NoConnector as BinaryConnector) { (_ /*formWrapper*/, bin) ->
        tokenizer.peek().let {
            BinaryConnector.forString(it)?.let { connector ->
                if (bin === BinaryConnector.NoConnector) {
                    tokenizer.popToken()
                    TptpUnitaryFof.parse(tokenizer, bvs) to connector
                } else if (connector === bin) {
                    if (bin is BinaryConnector.AssociativeBinaryConnector) {
                        tokenizer.popToken()
                        TptpUnitaryFof.parse(tokenizer, bvs) to bin
                    } else {
                        error(tokenizer.finishMessage("The connector '$connector' is not associative"))
                    }
                } else {
                    error(tokenizer.finishMessage("Expected '$bin.connector' but found '$connector.connector'"))
                }
            } ?: if (it == ")" || it == ",") {
                null
            } else {
                error(tokenizer.finishMessage("Unexpected: '$it'"))
            }
        }
    }.toList().run {
        when (last().second) {
            BinaryConnector.NoConnector ->
                TptpFormulaWrapper(first().first.formula, Atomic)

            BinaryConnector.AssociativeBinaryConnector.OrConnector ->
                TptpFormulaWrapper(Or(*this.map {
                    it.first.formula
                }.toTypedArray()), Assoc)
            BinaryConnector.AssociativeBinaryConnector.AndConnector ->
                TptpFormulaWrapper(And(*this.map {
                    it.first.formula
                }.toTypedArray()), Assoc)

            BinaryConnector.NonAssociativeBinaryConnector.IffConnector ->
                TptpFormulaWrapper(Iff(first().first.formula, last().first.formula), NonAssoc)
            BinaryConnector.NonAssociativeBinaryConnector.NandConnector ->
                TptpFormulaWrapper(Not(And(first().first.formula, last().first.formula)), NonAssoc)
            BinaryConnector.NonAssociativeBinaryConnector.NorConnector ->
                TptpFormulaWrapper(Not(Or(first().first.formula, last().first.formula)), NonAssoc)
            BinaryConnector.NonAssociativeBinaryConnector.XorConnector ->
                TptpFormulaWrapper(Not(Iff(first().first.formula, last().first.formula)), NonAssoc)
            BinaryConnector.NonAssociativeBinaryConnector.ReverseImpliesConnector ->
                TptpFormulaWrapper(Implies(last().first.formula, first().first.formula), NonAssoc)
            BinaryConnector.NonAssociativeBinaryConnector.ImpliesConnector ->
                TptpFormulaWrapper(Implies(first().first.formula, last().first.formula), NonAssoc)
            else ->
                error(tokenizer.finishMessage("Should not be possible"))
        }
    }
}

data class Functor(val cons: String, val args: List<Functor>) {
    fun toFormula(bvs: Set<BoundVariable>): TptpFormulaWrapper =
            if (args.isEmpty())
                TptpFormulaWrapper(Prop(cons), Atomic)
            else
                args.map { it.toTerm(bvs) }.toList().let {
                    TptpFormulaWrapper(Pred(cons, it.size)(it), Atomic)
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

object TptpUnitaryFof : FofInnerParser<TptpFormulaWrapper> {
    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): TptpFormulaWrapper =
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
                                TptpFormulaWrapper(Not(Equals(first.toTerm(bvs), Functor.parse(tokenizer).toTerm(bvs))), Unary)
                            } else if (it == "=") {
                                tokenizer.popToken()
                                TptpFormulaWrapper(Equals(first.toTerm(bvs), Functor.parse(tokenizer).toTerm(bvs)), Unary)
                            } else
                                first.toFormula(bvs)
                        }
                    }
                } else {
                    when (it) {
                        "?", "!" -> TptpFormulaWrapper(QuantifiedFormula.parse(tokenizer, bvs), Quantified)
                        "~" -> TptpFormulaWrapper(TptpFofNotParser.parse(tokenizer, bvs), Unary)
                        "[" -> error(tokenizer.finishMessage("Sequents not yet supported"))
                        "(" -> {
                            tokenizer.popToken()
                            TptpFormulaWrapper(TptpLogicFof.parse(tokenizer, bvs).formula, Paren).also {
                                TptpTokenizer.ensure(")", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
                            }
                        }
                        "\$true" -> {
                            tokenizer.popToken()
                            TptpFormulaWrapper(Truth, Atomic)
                        }
                        "\$false" -> {
                            tokenizer.popToken()
                            TptpFormulaWrapper(Falsity, Atomic)
                        }
                        else -> error(tokenizer.finishMessage("Unexpected character at beginning of FOF '${it}'"))
                    }
                }
            }
}

object TptpFofNotParser : FofInnerParser<Not> {
    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Not {
        TptpTokenizer.ensure("~", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
        return Not(TptpUnitaryFof.parse(tokenizer, bvs).formula)
    }
}

object QuantifiedFormula : FofInnerParser<VarBindingFormula> {
    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): VarBindingFormula =
            tokenizer.popToken().let {
                when (it) {
                    "!" -> {
                        parseBoundVars(tokenizer).let {
                            TptpTokenizer.ensure(":", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
                            ForAll(*it.toTypedArray(), formula = TptpUnitaryFof.parse(tokenizer, bvs + it).formula)
                        }
                    }
                    "?" -> {
                        parseBoundVars(tokenizer).let {
                            TptpTokenizer.ensure(":", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
                            ForSome(*it.toTypedArray(), formula = TptpUnitaryFof.parse(tokenizer, bvs + it).formula)
                        }
                    }
                    else -> error(tokenizer.finishMessage("Expecting '!' or '?', got '$it'"))
                }
            }

    private fun parseBoundVars(tokenizer: TptpTokenizer): Set<BoundVariable> {
        TptpTokenizer.ensure("[", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
        return generateSequence(parseBoundVar(tokenizer)) {
            tokenizer.popToken().let {
                when (it) {
                    "," -> parseBoundVar(tokenizer)
                    "]" -> null
                    else -> error(tokenizer.finishMessage("Expected ',' or ,], but got '$it'"))
                }
            }
        }.toSet()
    }

    private fun parseBoundVar(tokenizer: TptpTokenizer): BoundVariable =
            tokenizer.popToken().let {
                if (it.first().isUpperCase()) {
                    BV(it)
                } else
                    error(tokenizer.finishMessage("Expected upper-case word but got '$it'"))
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


