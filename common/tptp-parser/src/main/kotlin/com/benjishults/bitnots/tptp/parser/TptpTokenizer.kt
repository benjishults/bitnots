package com.benjishults.bitnots.tptp.parser

import java.io.BufferedReader

/**
 * The client is expected to close the reader.  This class is not thread safe.
 * @param predicates a map from predicate names to arity.  Propositional variables have arity 0.
 * @param functions a map from function names to arity.  Constants have arity 0.
 */
class TptpTokenizer(private val reader: BufferedReader, val fileName: String) { //, val predicates: Map<String, Int> = emptyMap(), val functions: Map<String, Int> = emptyMap()) {

    companion object {
        private val keywords = arrayOf("fof", "cnf", "thf", "tff", "include")
        val punctuation = listOf('(', ')', ',', '.', '[', ']', ':')

        private val operators = arrayOf("!", "?", "~", "&", "|", "<=>", "=>", "<=", "<->", "~|", "~&", "*", "+")
//        private val singleCharOperators = arrayOf('!', '?', '~', '&', '|', '*', '+')
        private val operatorStartChars = arrayOf('!', '?', '~', '&', '|', '<', '=', '*', '+')
//        private val operatorChars = arrayOf('!', '?', '~', '&', '|', '<', '=', '*', '+', '>', '-')

        //        private val predicates = arrayOf("!=", "\$true", "\$false")
        fun ensure(expected: String, actual: String, message: String = "Expected '$expected' but found '$actual'.") =
                if (actual != expected)
                    error(message)
                else
                    actual
    }

    private var lineNo: Int = 0

    // this will be the value of the last call to popChar()
    private var lastChar: Int = -1
    // after the first call to popChar(), this will always be the value that will next be returned by popChar().
    private var peekChar: Int = -1
    private var line: BufferedReader? = null
    private var atStartOfLine = true
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
        atStartOfLine = true
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
            }?.also {
                atStartOfLine = false
            } ?: run {
                atStartOfLine = true
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
        if (peekChar == -1)
            return
        else if (atStartOfLine && peekChar.toChar() == '%') {
            nextLine()
            return skipWhitespace()
        } else if (!peekChar.toChar().isWhitespace())
            return
        else {
            popChar()
            return skipWhitespace()
        }
    }

    fun peekKeyword(): String =
            peek().takeIf {
                it in keywords
            } ?: error(finishMessage("Keyword expected"))

    fun finishMessage(begin: String) = begin + " at line $lineNo of $fileName."

    private fun readOperator(): String = buildOperator(popChar().toChar().toString())

    tailrec private fun buildOperator(operator: String): String {
        val nextChar = popChar()
        if (nextChar != -1) {
            val longerOperator = operator + nextChar.toChar()
            if (operators.any { op -> op.startsWith(longerOperator) }) {
                return buildOperator(longerOperator)
            } else if (longerOperator in InnerParser.inequality) {
                return longerOperator
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
    private fun backup(): TptpTokenizer = this.also {
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
                    in operatorStartChars -> {
                        readOperator()
                    }
                    '\'' -> {
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
                    '"' -> {
                        // distinct_object
                        // return value should contain the outer double quotes
                        TODO(finishMessage("zero or more of ([\\40-\\41\\43-\\133\\135-\\176]|[\\\\][\"\\\\]) followed by \""))
                    }
                    '$' -> {
                        TODO(finishMessage("check for double dollar word or lower word"))
                    }
                    in 'a'..'z' -> {
                        readAlphaNumeric()
                    }
                    in 'A'..'Z' -> {
                        readAlphaNumeric()
                    }
                    in punctuation -> {
                        popChar()
                        it.toString()
                    }
                    in '0'..'9' -> {
                        readNumber()
                    }
                    '-' -> {
                        readNumber()
                    }
                    else -> {
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
                    ")" -> openParens--
                    "(" -> openParens++
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
        return generateSequence {
            popToken().let { token ->
                when (token) {
                    ")" -> null
                    "," -> token
                    else -> {
                        if (token.first().isLetter() || token.first().isDigit())
                            token
                        else
                            error(finishMessage("Unexpected token encountered: '$token'"))
                    }
                }
            }
        }.filter {
            it != ","
        }.toList()
    }

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
                                '_' -> {
                                    append(popChar().toChar())
                                    true
                                }
                                in '0'..'9' -> {
                                    append(popChar().toChar())
                                    true
                                }
                                else -> {
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
                                else -> {
                                    false
                                }
                            }
                        }
                }) {
                }
            }

}