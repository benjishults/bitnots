package com.benjishults.bitnots.parser

import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import java.io.BufferedReader
import java.nio.file.Path

/**
 * Implementations MUST override one of the parseFormula methods otherwise a stack overflow may occur.
 */
interface Parser<out O, T : Tokenizer, out F> {

    val tokenizerFactory: (BufferedReader, String) -> T

    fun parseFile(file: Path): O =
            file.toFile().reader().buffered().use {
                parse(tokenizerFactory(it, file.toString()))
            }

    fun parse(tokenizer: T): O

    fun parseAnnotatedFormula(tokenizer: T): AnnotatedFormula

    fun parseFormula(tokenizer: T): F =
            parseFormula(tokenizer, emptySet())

    fun parseFormula(tokenizer: T, bvs: Set<BoundVariable>): F =
            parseFormula(tokenizer)

}
