package com.benjishults.bitnots.parser

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import java.io.BufferedReader
import java.nio.file.Path

/**
 * Implementations MUST override one of the parseFormula methods otherwise a stack overflow may occur.
 */
interface Parser<out AF : AnnotatedFormula, T : Tokenizer, out F : Formula<*>> {

    val tokenizerFactory: (BufferedReader, String) -> T

    fun parseFile(file: Path): List<AF> =
            file.toFile().reader().buffered().use {
                parse(tokenizerFactory(it, file.toString()))
            }

    fun parse(tokenizer: T): List<AF>

    fun parseAnnotatedFormula(tokenizer: T): AF

    fun parseFormula(tokenizer: T): F =
            parseFormula(tokenizer, emptySet())

    fun parseFormula(tokenizer: T, bvs: Set<BoundVariable>): F =
            parseFormula(tokenizer)

}
