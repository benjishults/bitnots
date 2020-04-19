package com.benjishults.bitnots.sexpParser

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.ForAll
import com.benjishults.bitnots.model.formulas.fol.ForSome
import com.benjishults.bitnots.model.formulas.fol.Pred
import com.benjishults.bitnots.model.formulas.fol.equality.Equals
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Falsity
import com.benjishults.bitnots.model.formulas.propositional.Iff
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.formulas.propositional.Or
import com.benjishults.bitnots.model.formulas.propositional.Prop
import com.benjishults.bitnots.model.formulas.propositional.Tfae
import com.benjishults.bitnots.model.formulas.propositional.Truth
import com.benjishults.bitnots.model.terms.BV
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.model.terms.Const
import com.benjishults.bitnots.model.terms.FV
import com.benjishults.bitnots.model.terms.Fn
import com.benjishults.bitnots.model.terms.Term
import com.benjishults.bitnots.parser.Parser
import com.benjishults.bitnots.parser.Tokenizer
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import java.io.BufferedReader

object SexpParser : Parser<FolAnnotatedFormula, SexpTokenizer> {
    override val tokenizerFactory: (BufferedReader, String) -> SexpTokenizer
        get() = TODO()

    override fun parseFormula(tokenizer: SexpTokenizer, bvs: Set<BoundVariable>): Formula {
        SexpTokenizer.ensure(tokenizer.popToken(), "(")?.let { error(tokenizer.finishMessage(it)) }
        return tokenizer.peek().let {
            when (it) {
                // TODO make this more dynamic, configurable, and extensible
                "implies"                       -> {
                    tokenizer.popToken()
                    Implies(parseFormula(tokenizer, bvs), parseFormula(tokenizer, bvs)).also {
                        SexpTokenizer.ensure(tokenizer.popToken(), ")")?.let { error(tokenizer.finishMessage(it)) }
                    }
                }
                "iff"                           -> {
                    tokenizer.popToken()
                    Iff(parseFormula(tokenizer, bvs), parseFormula(tokenizer, bvs)).also {
                        SexpTokenizer.ensure(tokenizer.popToken(), ")")?.let { error(tokenizer.finishMessage(it)) }
                    }
                }
                "="                             -> {
                    tokenizer.popToken()
                    Equals(parseTerm(tokenizer, bvs), parseTerm(tokenizer, bvs)).also {
                        SexpTokenizer.ensure(tokenizer.popToken(), ")")?.let { error(tokenizer.finishMessage(it)) }
                    }
                }
                "not"                           -> {
                    tokenizer.popToken()
                    Not(parseFormula(tokenizer, bvs)).also {
                        SexpTokenizer.ensure(tokenizer.popToken(), ")")?.let { error(tokenizer.finishMessage(it)) }
                    }
                }
                "truth"                         -> {
                    tokenizer.popToken()
                    Truth.also {
                        SexpTokenizer.ensure(tokenizer.popToken(), ")")?.let { error(tokenizer.finishMessage(it)) }
                    }
                }
                "falsity"                       -> {
                    tokenizer.popToken()
                    Falsity.also {
                        SexpTokenizer.ensure(tokenizer.popToken(), ")")?.let { error(tokenizer.finishMessage(it)) }
                    }
                }
                "and"                           -> {
                    tokenizer.popToken()
                    And(*parseFormulasToCloseParen(tokenizer, bvs).toTypedArray())
                }
                "or"                            -> {
                    tokenizer.popToken()
                    Or(*parseFormulasToCloseParen(tokenizer, bvs).toTypedArray())
                }
                "tfae"                          -> {
                    tokenizer.popToken()
                    Tfae(*parseFormulasToCloseParen(tokenizer, bvs).toTypedArray())
                }
                "forall", "for-all"             -> {
                    tokenizer.popToken()
                    parseBoundVariables(tokenizer).let {
                        ForAll(*it.toTypedArray(), formula = parseFormula(tokenizer, bvs + it))
                    }.also {
                        SexpTokenizer.ensure(tokenizer.popToken(), ")")?.let { error(tokenizer.finishMessage(it)) }
                    }
                }
                "forsome", "for-some", "exists" -> {
                    tokenizer.popToken()
                    parseBoundVariables(tokenizer).let {
                        ForSome(*it.toTypedArray(), formula = parseFormula(tokenizer, bvs + it))
                    }.also {
                        SexpTokenizer.ensure(tokenizer.popToken(), ")")?.let { error(tokenizer.finishMessage(it)) }
                    }
                }
                else                            -> {
                    parseAtomicFormula(tokenizer, bvs)
                }
            }
        }
    }

    private fun parseBoundVariables(tokenizer: SexpTokenizer): Set<BoundVariable> {
        SexpTokenizer.ensure(tokenizer.popToken(), "(")?.let { error(tokenizer.finishMessage(it)) }
        return generateSequence(parseBoundVar(tokenizer)) {
            tokenizer.peek().let {
                when (it) {
                    "("  -> parseBoundVar(tokenizer)
                    ")"  -> null
                    else -> error(tokenizer.finishMessage("Expected '(' or ')' but got '$it'"))
                }
            }
        }.toSet().also {
            SexpTokenizer.ensure(tokenizer.popToken(), ")")?.let { error(tokenizer.finishMessage(it)) }
        }
    }

    private fun parseBoundVar(tokenizer: SexpTokenizer): BoundVariable {
        SexpTokenizer.ensure(tokenizer.popToken(), "(")?.let { error(tokenizer.finishMessage(it)) }
        return tokenizer.popToken().let {
            if (it.first().isLetter()) {
                BV(it)
            } else
                error(tokenizer.finishMessage("Expected variable-name but got '$it'"))
        }.also {
            SexpTokenizer.ensure(tokenizer.popToken(), ")")?.let { error(tokenizer.finishMessage(it)) }
        }
    }

    private fun parseFormulasToCloseParen(tokenizer: SexpTokenizer, bvs: Set<BoundVariable>): List<Formula> {
        return generateSequence {
            tokenizer.peek().takeIf {
                it != ")"
            }?.let {
                parseFormula(tokenizer, bvs)
            } ?: null.also {
                SexpTokenizer.ensure(tokenizer.popToken(), ")")?.let { error(tokenizer.finishMessage(it)) }
            }
        }.toList()
    }

    fun parseAtomicFormula(tokenizer: SexpTokenizer, bvs: Set<BoundVariable>): Formula {
        // NOTE open paren has already been consumed
        return tokenizer.popToken().let { functor ->
            require(functor.first().isLetter()) {
                tokenizer.finishMessage("Expected functor when parsing '$functor'")
            }
            generateSequence {
                tokenizer.peek().takeIf {
                    it != ")"
                }?.let {
                    parseTerm(tokenizer, bvs)
                } ?: null.also {
                    SexpTokenizer.ensure(tokenizer.popToken(), ")")?.let { error(tokenizer.finishMessage(it)) }
                }
            }.toList().let { terms ->
                if (terms.isEmpty()) {
                    Prop(functor)
                } else {
                    Pred(functor, terms.size)(terms)
                }
            }
        }
    }

    fun parseTerm(tokenizer: SexpTokenizer, bvs: Set<BoundVariable>): Term {
        // NOTE the open paren (if any) has NOT been consumed
        return tokenizer.popToken().let { first ->
            when (first.first()) {
                in 'a'..'z', in 'A'..'Z' -> {
                    if (first in bvs.map { it.cons.name }) {
                        BV(first)
                    } else {
                        FV(first)
                    }
                }
                '('                      -> {
                    tokenizer.popToken().let { functor ->
                        generateSequence {
                            tokenizer.peek().takeIf {
                                it != ")"
                            }?.let {
                                parseTerm(tokenizer, bvs)
                            } ?: null.also {
                                SexpTokenizer.ensure(tokenizer.popToken(), ")")
                                    ?.let { error(tokenizer.finishMessage(it)) }
                            }
                        }.toList().let { terms ->
                            if (terms.isEmpty()) {
                                Const(functor)
                            } else {
                                Fn(functor, terms.size)(terms)
                            }
                        }
                    }
                }
                else                     -> error(tokenizer.finishMessage("Expecting '(' or variable name, but found '$first'"))
            }
        }
    }

    override fun parse(tokenizer: SexpTokenizer): List<FolAnnotatedFormula> {
        TODO("Not yet implemented")
    }

    override fun parseAnnotatedFormula(tokenizer: SexpTokenizer): FolAnnotatedFormula {
        TODO("Not yet implemented")
    }

}

class SexpTokenizer(private val reader: BufferedReader, val fileName: String) : Tokenizer {

    companion object {
        val UNEXPECTED_END_OF_INPUT = "Unexpected end of input"
        fun ensure(expected: String, actual: String, message: String = "Expected '$expected' but found '$actual'.") =
            if (actual != expected)
                message
            else
                null
    }

    private var lineNo: Int = 0

    // this will be the value of the last call to popChar()
    private var lastChar: Int = -1

    // after the first call to popChar(), this will always be the value that will next be returned by popChar().
    private var peekChar: Int = -1
    private var line: BufferedReader? = null
    private var backup: String? = null
    private var lastToken: String? = null

    init {
        nextLine()
        skipWhitespace()
    }

    /**
     * After a call here, peekChar will return the first character of the next line.
     */
    private fun nextLine() {
        line?.close()
        line = reader.readLine()?.reader()?.buffered()
        lineNo++
        // this will clear out what would have been the next char and prepares us for the new line
        popChar()
    }

    /**
     * moves the cursor to the next character in the stream.  This will skip newlines.
     * The first call to this will erroneously return -1 so we get that out of the way in initialization.
     */
    private fun popChar(): Int {
        lastChar = peekChar
        peekChar = line?.let { line ->
            line.read().takeIf {
                it != -1
            } ?: run {
                generateSequence {
                    line.close()
                    this.line = reader.readLine()?.reader()?.buffered()
                    lineNo++
                    this.line.let { line ->
                        if (line === null) {
                            peekChar = -1
                            null
                        } else {
                            line.read().let {
                                if (it == -1) {
                                    -1
                                } else {
                                    peekChar = it
                                    null
                                }
                            }
                        }
                    }
                }.forEach {}
                peekChar
            } // can't be null
        } ?: -1
        return lastChar
    }

    // This will move the cursor (if necessary) so that peekChar is a non-whitespace.  Only works after the first call to popChar().
    private tailrec fun skipWhitespace() {
        if (peekChar == -1) {
            return
        } else if (!peekChar.toChar().isWhitespace())
            return
        else {
            popChar()
            return skipWhitespace()
        }
    }

    fun finishMessage(begin: String) = begin + " at line $lineNo of $fileName."

    /**
     * allows to backup up to a single token
     */
    private fun backup(): SexpTokenizer = this.also {
        check(backup === null && lastToken !== null) { finishMessage("backup improperly requested") }
        backup = lastToken
    }

    fun peek(): String = backup ?: popToken().also { backup() }

    // only works after first call to popChar() so we take care of that in the initializer.
    fun popToken(): String {
        return backup?.also { backup = null } ?: run {
            check(peekChar != -1) { finishMessage(UNEXPECTED_END_OF_INPUT) }
            peekChar.toChar().let {
                when (it) {
                    '\''          -> {
                        // return value should not contain the outer single quotes
                        //                        TODO("one or more of ([\\40-\\46\\50-\\133\\135-\\176]|[\\\\]['\\\\]) followed by '")
                        popChar()
                        buildString {
                            append(readAlphaNumeric())
                            while (peekChar != -1 && peekChar.toChar() != '\'') {
                                append(popChar().toChar())
                                append(readAlphaNumeric())
                            }
                            if (peekChar != -1 && peekChar.toChar() == '\'')
                                popChar()
                        }
                    }
                    '(', ')', '=' -> {
                        popChar().toChar().toString()
                    }
                    in 'a'..'z'   -> {
                        readAlphaNumeric()
                    }
                    in 'A'..'Z'   -> {
                        readAlphaNumeric()
                    }
                    in '0'..'9'   -> {
                        readNumber()
                    }
                    '-'           -> {
                        readNumber()
                    }
                    else          -> {
                        error(finishMessage("I was not prepared for '$it'"))
                    }
                }
            }.also {
                skipWhitespace()
                backup = null
                lastToken = it
            }
        }
    }

    fun hasNextToken(): Boolean = backup !== null || peekChar != -1

    private fun readAlphaNumeric(): String =
        buildString {
            while (peekChar.let {
                    if (it == -1) {
                        false
                    } else
                        it.toChar().let {
                            when (it) {
                                in 'a'..'z' -> {
                                    append(popChar().toChar())
                                    true
                                }
                                in 'A'..'Z' -> {
                                    append(popChar().toChar())
                                    true
                                }
                                '_', '-'    -> {
                                    append(popChar().toChar())
                                    true
                                }
                                in '0'..'9' -> {
                                    append(popChar().toChar())
                                    true
                                }
                                else        -> {
                                    false
                                }
                            }
                        }
                }) {
            }
        }

    private fun readNumber(): String =
        buildString {
            while (peekChar.let {
                    if (it == -1) {
                        false
                    } else
                        it.toChar().let {
                            when (it) {
                                in '0'..'9' -> {
                                    append(popChar().toChar())
                                    true
                                }
                                else        -> {
                                    false
                                }
                            }
                        }
                }) {
            }
        }

}
