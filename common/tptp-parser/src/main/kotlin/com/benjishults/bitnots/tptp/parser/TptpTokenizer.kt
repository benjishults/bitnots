package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.parser.Tokenizer
import java.io.BufferedReader

/**
 * The client is expected to close the reader.  This class is not thread safe.
 * @param predicates a map from predicate names to arity.  Propositional variables have arity 0.
 * @param functions a map from function names to arity.  Constants have arity 0.
 */
open class TptpTokenizer(private val reader: BufferedReader, private val fileName: String) : Tokenizer {

    companion object {
        val keywords = arrayOf("fof", "cnf", "thf", "tff", "include")
        val punctuation = listOf('(', ')', ',', '.', '[', ']', ':')

        val operators = arrayOf("!", "?", "~", "&", "|", "<=>", "=>", "<=", "<~>", "~|", "~&", "*", "+", "=", "!=")

        //        private val singleCharOperators = arrayOf('!', '?', '~', '&', '|', '*', '+')
        val operatorStartChars = arrayOf('!', '?', '~', '&', '|', '<', '=', '*', '+')
        //        private val operatorChars = arrayOf('!', '?', '~', '&', '|', '<', '=', '*', '+', '>', '-')

        //        private val predicates = arrayOf("!=", "\$true", "\$false")
        fun ensure(expected: String, actual: String, message: String = "Expected '$expected' but found '$actual'.") =
                if (actual != expected)
                    message
                else null
    }

    protected var lineNo: Int = 0

    // this will be the value of the last call to popChar()
    protected var previousChar: Int = -1

    // after the first call to popChar(), this will always be the value that will next be returned by popChar().
    protected var peekChar: Int = -1
    protected var line: String? = null
    protected var indexInLine = -1
    protected var atStartOfLine = true
    protected var backup: String? = null
    protected var lastToken: String? = null

    init {
        nextLine()
        skipWhitespace()
    }

    /**
     * After a call here, peekChar will return the first character of the next line.
     */
    protected fun nextLine() {
        line = reader.readLine()
        lineNo++
        indexInLine = 0
        // this will clear out what would have been the next char and prepares us for the new line
        popChar()
        atStartOfLine = true
    }

    /**
     * moves the cursor to the next character in the stream.  This will skip newlines.
     * The first call to this will erroneously return -1 so we get that out of the way in initialization.
     */
    protected fun popChar(): Int {
        previousChar = peekChar
        line?.let { nonNullLine ->
            if (nonNullLine.length > indexInLine) {
                atStartOfLine = false
                peekChar = nonNullLine[indexInLine++].toInt()
            } else {
                atStartOfLine = true
                indexInLine = 0
                do {
                    this.line = reader.readLine()
                    this.line?.let {nextLine ->
                        lineNo++
                        if (nextLine.isNotEmpty()) {
                            peekChar = nextLine[indexInLine++].toInt()
                            return previousChar
                        }
                    } ?: run {
                        peekChar = -1
                        null
                    }
                } while (true)
            }
        } ?: run {
            peekChar = -1
        }
        return previousChar
    }

    // This will move the cursor (if necessary) so that peekChar is a non-whitespace.  Only works after the first call to popChar().
    protected open fun skipWhitespace() {
        while (peekChar != -1) {
            if (atStartOfLine && peekChar.toChar() == '%') {
                nextLine()
                // return skipWhitespace()
            } else if (!peekChar.toChar().isWhitespace())
                return
            else {
                popChar()
                // return skipWhitespace()
            }
        }
    }

    fun peekKeyword(): String =
            peek().takeIf {
                it in keywords
            } ?: error(finishMessage("Keyword expected"))

    fun finishMessage(begin: String) = begin + " at line $lineNo of $fileName."

    protected fun readOperator(): String = buildOperator(popChar().toChar().toString())

    tailrec protected fun buildOperator(operator: String): String {
        val nextChar = popChar()
        if (nextChar != -1) {
            val longerOperator = operator + nextChar.toChar()
            if (operators.any { op -> op.startsWith(longerOperator) }) {
                return buildOperator(longerOperator)
            } else if (operator in operators) {
                return operator
            } else {
                error(finishMessage("Unexpected operator '$longerOperator'"))
            }
        } else if (operator in operators)
            return operator
        else
            error(finishMessage("Unexpected end of stream at '$operator'"))
    }

    /**
     * allows to backup up to a single token
     */
    protected fun backup(): TptpTokenizer = this.also {
        check(backup === null && lastToken !== null) { finishMessage("backup improperly requested") }
        // indexInLine--
        backup = lastToken
    }

    fun peek(): String =
            backup ?: popToken().also { backup() }

    // only works after first call to popChar() so we take care of that in the initializer.
    fun popToken(): String {
        return backup?.also { backup = null } ?: run {
            check(peekChar != -1) { finishMessage(Tokenizer.UNEXPECTED_END_OF_INPUT) }
            peekChar.toChar().let {
                when (it) {
                    in operatorStartChars -> {
                        readOperator()
                    }
                    '\''                  -> {
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
                    '"'                   -> {
                        // distinct_object
                        // return value should contain the outer double quotes
                        TODO(finishMessage(
                                "zero or more of ([\\40-\\41\\43-\\133\\135-\\176]|[\\\\][\"\\\\]) followed by \""))
                    }
                    '$'                   -> {
                        //                        TODO(finishMessage("check for double dollar word or lower word"))
                        popChar()
                        it + readAlphaNumeric()
                    }
                    in 'a'..'z'           -> {
                        readAlphaNumeric()
                    }
                    in 'A'..'Z'           -> {
                        readAlphaNumeric()
                    }
                    in punctuation        -> {
                        popChar()
                        it.toString()
                    }
                    in '0'..'9'           -> {
                        readNumber()
                    }
                    '-'                   -> {
                        readNumber()
                    }
                    else                  -> {
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

    /**
     * moves the cursor past the next unmatched close paren from here
     */
    fun moveToEndParen() {
        backup = null
        var openParens = 1
        while (openParens > 0) {
            try {
                when (popToken()) {
                    ")"  -> openParens--
                    "("  -> openParens++
                    else -> {
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Assumes that the cursor is looking at a comma or a string followed by a comma-separated list of words followed by a close paren.  Returns the list of strings.
     * Moves the cursor past the next unmatched close paren from here
     */
    fun parseCommaSeparatedListOfStringsToEndParen(): List<String> {
        backup = null
        var openParens = 1
        return generateSequence {
            popToken().let { token ->
                when (token) {
                    ")"  -> {
                        openParens--
                        token.takeIf {
                            openParens != 0
                        }
                    }
                    "("  -> {
                        openParens++
                        token
                    }
                    ","  -> token
                    else -> {
                        //                        if (token.first().isLetter() || token.first().isDigit())
                        token
                        //                        else
                        //                            error(finishMessage("Unexpected token encountered: '$token'"))
                    }
                }
            }
        }.filter {
            it != ","
        }.toList()
    }

    /**
     * Assumes that the cursor is looking at a comma or a string followed by a comma-separated list of words followed by a close paren.  Returns the list of strings.
     * Moves the cursor past the next unmatched close paren from here
     */
    fun skipToEndParen() {
        backup = null
        var openParens = 1
        while (openParens != 0) {
            val token = popToken()
            when (token) {
                ")"  -> {
                    --openParens
                }
                "("  -> {
                    openParens++
                }
                else -> {
                }
            }
        }
    }

    protected fun readAlphaNumeric(): String =
            buildString {
                loop@ while (peekChar != -1) {
                    when (peekChar.toChar()) {
                        in 'a'..'z' -> {
                            append(popChar().toChar())
                        }
                        in 'A'..'Z' -> {
                            append(popChar().toChar())
                        }
                        '_'         -> {
                            append(popChar().toChar())
                        }
                        in '0'..'9' -> {
                            append(popChar().toChar())
                        }
                        else        -> {
                            break@loop
                        }
                    }
                }
            }

    protected fun readNumber(): String =
            buildString {
                append(popChar().toChar())
                while (peekChar in '0'.toInt()..'9'.toInt()) {
                    append(popChar().toChar())
                }
            }

}
