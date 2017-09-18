package com.benjishults.bitnots.tptp.parser

import java.io.BufferedReader

/**
 * The client is expected to close the reader.
 * @param predicates a map from predicate names to arity.  Propositional variables have arity 0.
 * @param functions a map from function names to arity.  Constants have arity 0.
 */
class TptpTokenizer(val reader: BufferedReader, val fileName: String) { //, val predicates: Map<String, Int> = emptyMap(), val functions: Map<String, Int> = emptyMap()) {

    companion object {
        private val keywords = arrayOf("fof", "cnf", "thf", "tff", "include")
        val punctuation = listOf('(', ')', ',', '.', '[', ']', ':')

        private val operators = arrayOf("!", "?", "~", "&", "|", "<=>", "=>", "<=", "<->", "~|", "~&", "*", "+")
        private val singleCharOperators = arrayOf('!', '?', '~', '&', '|', '*', '+')
        private val operatorStartChars = arrayOf('!', '?', '~', '&', '|', '<', '=', '*', '+')
        private val operatorChars = arrayOf('!', '?', '~', '&', '|', '<', '=', '*', '+', '>', '-')

        private val predicates = arrayOf("!=", "\$true", "\$false")
        fun ensure(expected: String, actual: String, message: String = "Expected '$expected' but found '$actual'.") =
                if (actual != expected)
                    error(message)
                else
                    actual
    }

    private var lineNo: Int = 0

    private var nextChar: Int = -1
    private var line: BufferedReader? = null
    private var atStartOfLine = true
    private var backup: String? = null
    private var lastToken: String? = null

    init {
        nextLine()
        skipWhitespace()
    }

    /**
     * moves the cursor to the beginning of the next line
     */
    private fun nextLine() {
        line = reader.readLine()?.reader()?.buffered()
        lineNo++
        nextChar()
        atStartOfLine = true
    }

    /**
     * moves the cursor to the next character in the stream.  This will skip newlines.
     */
    private fun nextChar(): Int {
        nextChar = line?.let { line ->
            line.read().takeIf {
                it != -1
            }?.also {
                atStartOfLine = false
            } ?: run {
                atStartOfLine = true
                generateSequence {
                    this.line = reader.readLine()?.reader()?.buffered()
                    lineNo++
                    this.line.let { line ->
                        if (line === null) {
                            this.nextChar = -1
                            null
                        } else {
                            line.read().let {
                                if (it == -1) {
                                    -1
                                } else {
                                    nextChar = it
                                    null
                                }
                            }
                        }
                    }
                }.forEach {}
                nextChar
            } // can't be null
        } ?: -1
        return nextChar
    }

    private tailrec fun skipWhitespace() {
        if (nextChar == -1)
            return
        else if (atStartOfLine && nextChar.toChar() == '%') {
            nextLine()
            return skipWhitespace()
        } else if (!nextChar.toChar().isWhitespace())
            return
        else {
            nextChar()
            return skipWhitespace()
        }
    }

    fun peekKeyword(): String =
            peek().takeIf {
                it in keywords
            } ?: error(finishMessage("Keyword expected"))


    fun finishMessage(begin: String) = begin + " at line $lineNo or $fileName."

    private fun readOperator(): String = buildOperator(nextChar.toChar().toString())

    private fun buildOperator(operator: String): String =
            nextChar().takeIf { it != -1 }?.let { char ->
                (operator + char.toChar().toString()).let {
                    if (operators.any { op -> op.startsWith(it) }) {
                        return buildOperator(it)
                    } else if (operator in operators) {
                        return operator
                    } else {
                        error(finishMessage("Unexpected operator '$it'"))
                    }
                }
            } ?: operator.takeIf { it in operators } ?: error(finishMessage("Unexpected end of stream at '$operator'"))

    /**
     * allows to backup up to a single token
     */
    private fun backup(): TptpTokenizer = this.also {
        check(backup === null && lastToken !== null) { finishMessage("backup improperly requested") }
        backup = lastToken
    }

    fun peek(): String = backup ?: popToken().also { backup() }

    fun popToken(): String {
        return backup?.also { backup = null } ?: run {
            check(nextChar != -1) { finishMessage(UNEXPECTED_END_OF_INPUT) }
            nextChar.toChar().let {
                when (it) {
                    in operatorStartChars -> {
                        readOperator()
                    }
                    '\'' -> {
                        // return value should not contain the outer single quotes
//                        TODO("one or more of ([\\40-\\46\\50-\\133\\135-\\176]|[\\\\]['\\\\]) followed by '")
                        nextChar()
                        buildString {
                            append(readAlphaNumeric())
                            while (nextChar.toChar() != '\'') {
//                                append(nextChar.toChar())
                                append(readAlphaNumeric())
                            }
                        }.also {
                            nextChar()
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
                        nextChar()
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

    fun hasNextToken(): Boolean = backup !== null || nextChar != -1

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
                append(nextChar.toChar())
                while (nextChar().let {
                    if (it == -1) {
                        false
                    } else
                        it.toChar().let {
                            when (it) {
                                in 'a'..'z' -> {
                                    append(it)
                                    true
                                }
                                in 'A'..'Z' -> {
                                    append(it)
                                    true
                                }
                                '_' -> {
                                    append(it)
                                    true
                                }
                                in '0'..'9' -> {
                                    append(it)
                                    true
                                }
                                in punctuation -> {
                                    false
                                }
                                else -> {
                                    skipWhitespace()
                                    false
                                }
                            }
                        }
                }) {
                }
            }

    private fun readNumber(): String =
            buildString {
                append(nextChar.toChar())
                while (nextChar().let {
                    if (it == -1) {
                        false
                    } else
                        it.toChar().let {
                            when (it) {
                                in '0'..'9' -> {
                                    append(it)
                                    true
                                }
                                in punctuation -> {
                                    false
                                }
                                else -> {
                                    skipWhitespace()
                                    false
                                }
                            }
                        }
                }) {
                }
            }

}