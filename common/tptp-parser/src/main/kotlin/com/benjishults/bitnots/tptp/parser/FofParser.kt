package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import com.benjishults.bitnots.model.formulas.fol.equality.Equals
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Falsity
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.propositional.Truth
import com.benjishults.bitnots.model.terms.BV
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.parser.Tokenizer
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRoles

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

/**
 * NOTE: In TPTP, function symbols and predicate symbols should be disjoint in order to avoid parsing trouble.
 */
object TptpFofParser : AbstractTptpParser<Formula<*>>() {
    override val annotatedFormulaFactory = FolAnnotatedFormula::class.constructors.first()
    override val formulaType: String = "fof"

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
                                "fof" -> add(parseAnnotatedFormula(tokenizer))
                                "include" -> addAll(Include.parse(tokenizer, TptpFofParser))
                                else -> error(tokenizer.finishMessage("Parsing for '${it}' not yet implemented"))
                            }
                        }
                    }
                })
    }

//    override fun parse(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Formula<*> =
//            tokenizer.peek().let {
//                if (it.first().isLetter()) {
//                    // this could be an inequality so, parse a functor, check the next thing, and behave appropriately
//                    Functor.parse(tokenizer).let { first ->
//                        tokenizer.peek().let {
//                            when (it) {
//                                "!=" -> {
//                                    // TODO unreachable?
//                                    tokenizer.popToken()
//                                    parseAfterUnitaryFormula(Not(Equals(first.toTerm(bvs), Functor.parse(tokenizer).toTerm(bvs)))/*, Unary*/, tokenizer, bvs)
//                                }
//                                "=" -> {
//                                    // TODO unreachable?
//                                    tokenizer.popToken()
//                                    parseAfterUnitaryFormula(Equals(first.toTerm(bvs), Functor.parse(tokenizer).toTerm(bvs))/*, Unary*/, tokenizer, bvs)
//                                }
//                                else -> {
//                                    // TODO check whether I ever use the second component of the formWrapper
//                                    parseAfterUnitaryFormula(first.toFormula(bvs), tokenizer, bvs)
//                                }
//                            }
//                        }
//                    }
//                } else if (it in InnerParser.unitaryFormulaInitial) {
//                    parseAfterUnitaryFormula(parseUnitaryFof(tokenizer, bvs), tokenizer, bvs)
//                } else if (it == "\$true") {
//                    tokenizer.popToken()
//                    parseAfterUnitaryFormula(Truth/*, Atomic*/, tokenizer, bvs)
//                } else if (it == "\$false") {
//                    tokenizer.popToken()
//                    parseAfterUnitaryFormula(Falsity/*, Atomic*/, tokenizer, bvs)
//                } else {
//                    error(tokenizer.finishMessage("Unexpected character at beginning of logic FOF: '${it}'"))
//                }
//            }

    override fun parseFormula(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Formula<*> =
            tokenizer.peek().let {
                if (it == "[")
                    error(tokenizer.finishMessage("Sequents not yet supported"))
                else
                    parseLogicFof(tokenizer, bvs)
            }

    fun parseFofNot(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Not {
        TptpTokenizer.ensure("~", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
        return Not(parseUnitaryFof(tokenizer, bvs))
    }

    fun parseQuantifiedFormula(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): VarBindingFormula =
            tokenizer.popToken().let {
                when (it) {
                    "!" -> {
                        parseBoundVars(tokenizer).let {
                            TptpTokenizer.ensure(":", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
                            ForAll(*it.toTypedArray(), formula = parseUnitaryFof(tokenizer, bvs + it))
                        }
                    }
                    "?" -> {
                        parseBoundVars(tokenizer).let {
                            TptpTokenizer.ensure(":", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
                            ForSome(*it.toTypedArray(), formula = parseUnitaryFof(tokenizer, bvs + it))
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

    fun parseLogicFof(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Formula<*> =
            tokenizer.peek().let {
                if (it.first().isLetter()) {
                    // this could be an inequality so, parse a functor, check the next thing, and behave appropriately
                    Functor.parse(tokenizer).let { first ->
                        tokenizer.peek().let {
                            when (it) {
                                "!=" -> {
                                    // TODO unreachable?
                                    tokenizer.popToken()
                                    parseAfterUnitaryFormula(Not(Equals(first.toTerm(bvs), Functor.parse(tokenizer).toTerm(bvs))/*, Unary*/), tokenizer, bvs)
                                }
                                "=" -> {
                                    // TODO unreachable?
                                    tokenizer.popToken()
                                    parseAfterUnitaryFormula(Equals(first.toTerm(bvs), Functor.parse(tokenizer).toTerm(bvs)/*, Unary*/), tokenizer, bvs)
                                }
                                else -> {
                                    // TODO check whether I ever use the second component of the formWrapper
                                    parseAfterUnitaryFormula(first.toFormula(bvs), tokenizer, bvs)
                                }
                            }
                        }
                    }
                } else if (it in InnerParser.unitaryFormulaInitial) {
                    parseAfterUnitaryFormula(parseUnitaryFof(tokenizer, bvs), tokenizer, bvs)
                } else if (it == "\$true") {
                    tokenizer.popToken()
                    parseAfterUnitaryFormula(Truth/*, Atomic*/, tokenizer, bvs)
                } else if (it == "\$false") {
                    tokenizer.popToken()
                    parseAfterUnitaryFormula(Falsity/*, Atomic*/, tokenizer, bvs)
                } else {
                    error(tokenizer.finishMessage("Unexpected character at beginning of logic FOF: '${it}'"))
                }
            }

    fun parseAfterUnitaryFormula(initial: Formula<*>, tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Formula<*> {
        // TODO check whether I ever use the second component of the formWrapper
        return generateSequence(initial to BinaryConnector.NoConnector as BinaryConnector) { (_ /*formWrapper*/, bin) ->
            tokenizer.peek().let {
                BinaryConnector.forString(it)?.let { connector ->
                    if (bin === BinaryConnector.NoConnector) {
                        tokenizer.popToken()
                        parseUnitaryFof(tokenizer, bvs) to connector
                    } else if (connector === bin) {
                        if (bin is BinaryConnector.AssociativeBinaryConnector) {
                            tokenizer.popToken()
                            parseUnitaryFof(tokenizer, bvs) to bin
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
                    first().first/*, Atomic*/

                BinaryConnector.AssociativeBinaryConnector.OrConnector ->
                    Or(*this.map {
                        it.first
                    }.toTypedArray())/*, Assoc*/
                BinaryConnector.AssociativeBinaryConnector.AndConnector ->
                    And(*this.map {
                        it.first
                    }.toTypedArray())/*, Assoc*/

                BinaryConnector.NonAssociativeBinaryConnector.IffConnector ->
                    Iff(first().first, last().first)/*, NonAssoc*/
                BinaryConnector.NonAssociativeBinaryConnector.NandConnector ->
                    Not(And(first().first, last().first))/*, NonAssoc*/
                BinaryConnector.NonAssociativeBinaryConnector.NorConnector ->
                    Not(Or(first().first, last().first))/*, NonAssoc*/
                BinaryConnector.NonAssociativeBinaryConnector.XorConnector ->
                    Not(Iff(first().first, last().first))/*, NonAssoc*/
                BinaryConnector.NonAssociativeBinaryConnector.ReverseImpliesConnector ->
                    Implies(last().first, first().first)/*, NonAssoc*/
                BinaryConnector.NonAssociativeBinaryConnector.ImpliesConnector ->
                    Implies(first().first, last().first)/*, NonAssoc*/
                else ->
                    error(tokenizer.finishMessage("Should not be possible"))
            }
        }
    }

    fun parseUnitaryFof(tokenizer: TptpTokenizer, bvs: Set<BoundVariable>): Formula<*> =
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
                                Not(Equals(first.toTerm(bvs), Functor.parse(tokenizer).toTerm(bvs)))/*, Unary*/
                            } else if (it == "=") {
                                tokenizer.popToken()
                                Equals(first.toTerm(bvs), Functor.parse(tokenizer).toTerm(bvs))/*, Unary*/
                            } else
                                first.toFormula(bvs)
                        }
                    }
                } else {
                    when (it) {
                        "?", "!" -> parseQuantifiedFormula(tokenizer, bvs)/*, Quantified*/
                        "~" -> parseFofNot(tokenizer, bvs)/*, Unary*/
                        "[" -> error(tokenizer.finishMessage("Sequents not yet supported"))
                        "(" -> {
                            tokenizer.popToken()
                            parseLogicFof(tokenizer, bvs)/*, Paren*/.also {
                                TptpTokenizer.ensure(")", tokenizer.popToken())?.let { error(tokenizer.finishMessage(it)) }
                            }
                        }
                        "\$true" -> {
                            tokenizer.popToken()
                            Truth/*, Atomic*/
                        }
                        "\$false" -> {
                            tokenizer.popToken()
                            Falsity/*, Atomic*/
                        }
                        else -> error(tokenizer.finishMessage("Unexpected character at beginning of FOF '${it}'"))
                    }
                }
            }

}
