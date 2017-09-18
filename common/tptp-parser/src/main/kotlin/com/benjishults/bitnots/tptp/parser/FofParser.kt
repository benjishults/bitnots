package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import com.benjishults.bitnots.model.formulas.fol.equality.Equals
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.propositional.Prop
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

    abstract class NonAssociativeBinaryConnector(override val connector: String) : BinaryConnector(connector)
    abstract class AssociativeBinaryConnector(override val connector: String) : BinaryConnector(connector)

    object NoConnector : BinaryConnector(" ")

    object AndConnector : AssociativeBinaryConnector("&")
    object OrConnector : AssociativeBinaryConnector("|")

    object IffConnector : NonAssociativeBinaryConnector("<=>")
    object ImpliesConnector : NonAssociativeBinaryConnector("=>")
    object ReverseImpliesConnector : NonAssociativeBinaryConnector("<=")
    object XorConnector : NonAssociativeBinaryConnector("<->")
    object NorConnector : NonAssociativeBinaryConnector("~|")
    object NandConnector : NonAssociativeBinaryConnector("~&")

    companion object {
        fun values() = BinaryConnector::class.nestedClasses.filter { it.isFinal && !it.isCompanion }.map { it.objectInstance as BinaryConnector }.toList()
        fun forString(connector: String) = values().firstOrNull { it.connector == connector }
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
                if (it.first().isLetter() || it in InnerParser.unitaryFormulaInitial) {
                    generateSequence(TptpUnitaryFof.parse(tokenizer, bvs) to BinaryConnector.NoConnector as BinaryConnector) { (formWrapper, bin) ->
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

                            BinaryConnector.OrConnector ->
                                TptpFormulaWrapper(Or(*this.map {
                                    it.first.formula
                                }.toTypedArray()), Assoc)
                            BinaryConnector.AndConnector ->
                                TptpFormulaWrapper(And(*this.map {
                                    it.first.formula
                                }.toTypedArray()), Assoc)

                            BinaryConnector.IffConnector ->
                                TptpFormulaWrapper(Iff(first().first.formula, last().first.formula), NonAssoc)
                            BinaryConnector.NandConnector ->
                                TptpFormulaWrapper(Not(And(first().first.formula, last().first.formula)), NonAssoc)
                            BinaryConnector.NorConnector ->
                                TptpFormulaWrapper(Not(Or(first().first.formula, last().first.formula)), NonAssoc)
                            BinaryConnector.XorConnector ->
                                TptpFormulaWrapper(Not(Iff(first().first.formula, last().first.formula)), NonAssoc)
                            BinaryConnector.ReverseImpliesConnector ->
                                TptpFormulaWrapper(Implies(last().first.formula, first().first.formula), NonAssoc)
                            BinaryConnector.ImpliesConnector ->
                                TptpFormulaWrapper(Implies(first().first.formula, last().first.formula), NonAssoc)
                            else ->
                                error(tokenizer.finishMessage("Should not be possible"))
                        }
                    }
                } else {
                    error(tokenizer.finishMessage("Unexpected character at beginning of logic FOF: '${it}'"))
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
                    require(functor.first().isLetter()) { tokenizer.finishMessage("Expected functor when parsing '$functor'") }
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
                                TptpTokenizer.ensure(")", tokenizer.popToken())
                            }
                        }
                        else -> error(tokenizer.finishMessage("Unexpected character at beginning of FOF '${it}'"))
                    }
                }
            }
}

object TptpFofNotParser : FofInnerParser<Not> {
    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Not {
        TptpTokenizer.ensure("~", tokenizer.popToken())
        return Not(TptpUnitaryFof.parse(tokenizer, bvs).formula)
    }
}

object QuantifiedFormula : FofInnerParser<VarBindingFormula> {
    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): VarBindingFormula =
            tokenizer.popToken().let {
                when (it) {
                    "!" -> {
                        parseBoundVars(tokenizer).let {
                            TptpTokenizer.ensure(":", tokenizer.popToken())
                            ForAll(TptpUnitaryFof.parse(tokenizer, bvs + it).formula, *it.toTypedArray())
                        }
                    }
                    "?" -> {
                        parseBoundVars(tokenizer).let {
                            TptpTokenizer.ensure(":", tokenizer.popToken())
                            ForSome(TptpUnitaryFof.parse(tokenizer, bvs + it).formula, *it.toTypedArray())
                        }
                    }
                    else -> error(tokenizer.finishMessage("Expecting '!' or '?', got '$it'"))
                }
            }

    private fun parseBoundVars(tokenizer: TptpTokenizer): Set<BoundVariable> {
        TptpTokenizer.ensure("[", tokenizer.popToken())
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


