package com.benjishults.bitnots.parser

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.terms.BoundVariable
import com.benjishults.bitnots.parser.formula.FormulaClassifier
import com.benjishults.bitnots.parser.formula.HypsAndTargets
import com.benjishults.bitnots.theory.formula.AnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaForm
import java.io.BufferedReader
import java.nio.file.Path

/**
 * Implementations MUST override one of the parseFormula methods otherwise a stack overflow may occur.
 */
interface Parser<AF : AnnotatedFormula, T : Tokenizer> {

    val tokenizerFactory: (BufferedReader, String) -> T

    fun parseFile(file: Path): List<AF> =
        file.toFile().reader().buffered().use { reader ->
            parse(tokenizerFactory(reader, file.toString()))
        }

    fun parse(tokenizer: T): List<AF>

    fun parseAnnotatedFormula(tokenizer: T): AF

    fun parseFormula(tokenizer: T): Formula =
        parseFormula(tokenizer, emptySet())

    fun parseFormula(tokenizer: T, bvs: Set<BoundVariable>): Formula =
        parseFormula(tokenizer)

    fun <FF: FormulaForm> parseAndClassify(
        formulaClassifier: FormulaClassifier,
        fileDescriptor: FileDescriptor<FF, *>,
        fileFetcher: FileFetcher<*, FF, FileDescriptor<FF, *>>
    ): HypsAndTargets {
        return formulaClassifier.classify(
            fileDescriptor.parser<AF>().parseFile(fileFetcher.findProblemFile(fileDescriptor))
        )
    }

}
