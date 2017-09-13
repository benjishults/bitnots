package com.benjishults.bitnots.tptp.parser

import java.io.BufferedReader

/**
 * The client is expected to close the reader.
 * @param predicates a map from predicate names to arity.  Propositional variables have arity 0.
 * @param functions a map from function names to arity.  Constants have arity 0.
 */
class TptpTokenizer(val reader: BufferedReader) { //, val predicates: Map<String, Int> = emptyMap(), val functions: Map<String, Int> = emptyMap()) {

    companion object {
        private val keywords = arrayOf("fof", "cnf", "thf", "tff", "include")
        val punctuation = listOf('(', ')', ',', '.', '[', ']', ':')
        private val operators = arrayOf("!", "?", "~", "&", "|", "<=>", "=>", "<=", "<->", "~|", "~&", "*", "+")
        private val predicates = arrayOf("!=", "\$true", "\$false")
        fun ensure(expected: String, actual: String, message: String = "Expected '$expected' but found '$actual'.") {
            if (actual != expected)
                error(message)
        }
    }

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
     * allows to backup up to a single token
     */
    fun backup(): TptpTokenizer = this.also {
        check(backup === null && lastToken !== null) { "backup improperly requested" }
        backup = lastToken
    }

    /**
     * moves to next line of non-whitespace and puts cursor at the first non-whitespace character of the line
     */
    private fun nextLine() {
        line = reader.readLine()?.reader()?.buffered()
        nextChar()
        atStartOfLine = true
    }

    private fun nextChar(): Int {
        nextChar = line?.read().takeIf {
            it != -1
        }.also {
            atStartOfLine = false
        } ?: run {
            line = reader.readLine()?.reader()?.buffered()
            atStartOfLine = true
            line?.read() ?: -1
        }
        return nextChar
    }

    /**
     * @param atStartOfLine true if nextChar is known to be the first character of a line
     */
    private tailrec fun skipWhitespace() {
        if (nextChar == -1)
            return
        nextChar.toChar().let {
            if (atStartOfLine) {
                if (it == '%') {
                    nextLine()
                    skipWhitespace()
                }
                return
            } else if (!it.isWhitespace())
                return
            else {
                skipWhitespace()
                return
            }
        }
    }

    fun parseKeyword(): String {
        return nextToken().takeIf {
            it in keywords
        } ?: error("Keyword expected.")
    }

    fun nextToken(): String {
        return backup?.also { backup = null } ?: run {
            check(nextChar != -1)
            nextChar.toChar().let {
                when (it) {
                    '\'' -> {
                        // return value should not contain the outer single quotes
                        TODO("one or more of ([\\40-\\46\\50-\\133\\135-\\176]|[\\\\]['\\\\]) followed by '")
                    }
                    '"' -> {
                        // distinct_object
                        // return value should contain the outer double quotes
                        TODO("zero or more of ([\\40-\\41\\43-\\133\\135-\\176]|[\\\\][\"\\\\]) followed by \"")
                    }
                    '$' -> {
                        TODO("check for double dollar word or lower word")
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
                    else -> {
                        error("I was not prepared for '$it'.")
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
                when (nextToken()) {
                    ")" -> openParens--
                    "(" -> openParens++
                    else -> {
                    }
                }
            } catch (e: Exception) {
            }
        }
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

}