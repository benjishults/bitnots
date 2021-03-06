package com.benjishults.bitnots.test

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.prover.ProblemMeterRegistry
import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRole
import com.benjishults.bitnots.tptp.TptpProperties
import com.benjishults.bitnots.tptp.extension.fetchTimer
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFof
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import io.micrometer.core.instrument.Timer
import kotlinx.coroutines.runBlocking
import java.io.BufferedWriter
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

val result: AtomicReference<Result> = AtomicReference(Error(""))

val tptpReadResultsFolder: Path = Paths.get(TptpProperties.getReadResultsFolderName())
val tptpWriteResultsFolder: Path = Paths.get(TptpProperties.getWriteResultsFolderName())

const val millis = 1000L

object Regression {
    fun newBaselineFof() {
        createResults(
            "baseline${Instant.now().toEpochMilli()}.csv",
            listOf(*TptpDomain.values()),
            listOf(TptpFof),
            3,
            // stack overflow on parsing file
            TptpProblemFileDescriptor(TptpDomain.LCL, TptpFof, 680, 1, 15),
            // stack overflow on parsing file
            TptpProblemFileDescriptor(TptpDomain.LCL, TptpFof, 680, 1, 20),
            // didn't even try
            TptpProblemFileDescriptor(TptpDomain.LCL, TptpFof, 681, 1, 20),
            // didn't even try
            TptpProblemFileDescriptor(TptpDomain.LCL, TptpFof, 684, 1, 20),
            // didn't even try
            TptpProblemFileDescriptor(TptpDomain.LCL, TptpFof, 685, 1, 20),
            // Unexpected character at beginning of FOF '+' at line 30 of /usr/local/share/tptp/Problems/LCL/LCL882+1.p.
            TptpProblemFileDescriptor(TptpDomain.LCL, TptpFof, 882, 1)
        )
    }

    fun continueNewBaselineFromLastRunFof() {
        // TODO continue from where the last run stopped... appending to existing file
    }

    fun doAsWellAsAccepted() {
        // tptpReadResultsFolder.resolve("baseline.csv")
        //     .toFile()
        //     .reader()
        //     .use { acceptedResultsFile ->
        //         CSVParser(
        //                 acceptedResultsFile,
        //                 CSVFormat.DEFAULT.withHeader(*CsvHelper.HEADER.split(",").toTypedArray())
        //         ).use { parser ->
        //             // if it succeeded last time
        //             parser.filter { it.get("status") == "success" }
        //                 .forEach { record ->
        //                     val domain = TptpDomain.valueOf(record.get("domain"))
        //                     val form = TptpFormulaForm.valueOf(record.get("form"))
        //                     val number = record.get("number").toInt(10)
        //                     val version = record.get("version").toInt(10)
        //                     val size = record.get("size").toInt(10)
        //                     val acceptedMillis = record.get("millis").toDouble()
        //                     // val acceptedTimeOut = record.get("timeoutMillis").toInt(10)
        //                     val acceptedQLimit = record.get("q-limit").toLong(10)
        //                     val descriptor = TptpProblemFileDescriptor(domain, form, number, version, size)
        //                     classifyFormulas(
        //                             TptpFofParser.parseFile(
        //                                     TptpFileFetcher.findProblemFile(descriptor))
        //                     ).let { (hyps, targets) ->
        //                         val hypothesis = createConjunct(hyps)
        //                         targets.forEach { target ->
        //                             FolFormulaTableauProver(
        //                                     FolUnificationClosingStrategy(),
        //                                     FolStepStrategy(acceptedQLimit)
        //                             ).also { prover ->
        //                                 clearInternTables()
        //                                 val timer = fetchTimer(descriptor, "problem")
        //
        //                                 timer.record {
        //                                     limitedTimeProve(
        //                                             prover,
        //                                             hypothesis?.let {
        //                                                 Implies(it, target)
        //                                             } ?: target,
        //                                             acceptedMillis.toLong() + 1)
        //                                 }
        //                                 assert(result.get() === Proved)
        //                                 val max = timer.max(TimeUnit.MILLISECONDS)
        //                                 if (1.0 - (Math.abs(max - acceptedMillis) / acceptedMillis) > .1)
        //                                     println("This run ($max) was about as fast as the previous ($acceptedMillis) for $descriptor with q-limit $acceptedQLimit")
        //                                 else
        //                                     println("This run ($max) was significantly slower than the previous ($acceptedMillis) for $descriptor with q-limit $acceptedQLimit")
        //                             }
        //                         }
        //                     }
        //                 }
        //         }
        //     }
    }

}

object PushLimits {

    fun tryHigherQLimit() {
        // tptpWriteResultsFolder.resolve("results${Instant.now().toEpochMilli()}.csv")
        //     .toFile()
        //     .outputStream()
        //     .bufferedWriter()
        //     .use { resultsFile ->
        //         writeCsvHeader(resultsFile)
        //
        //         tptpReadResultsFolder.resolve("results.csv")
        //             .toFile()
        //             .reader()
        //             .use { acceptedResultsFile ->
        //                 CSVParser(
        //                         acceptedResultsFile,
        //                         CSVFormat.DEFAULT.withHeader(*CsvHelper.HEADER.split(",").toTypedArray())
        //                 ).use { parser ->
        //                     // if it succeeded last time
        //                     parser.filter { it.get("status") == "fail" }
        //                         .filter { it.get("millis").toDouble() < (it.get("timeoutMillis").toDouble() * 0.75) }
        //                         .forEach { record ->
        //                             val domain = TptpDomain.valueOf(record.get("domain"))
        //                             val form = TptpFormulaForm.valueOf(record.get("form"))
        //                             val number = record.get("number").toInt(10)
        //                             val version = record.get("version").toInt(10)
        //                             val size = record.get("size").toInt(10)
        //                             // val acceptedMillis = record.get("millis").toDouble()
        //                             // val acceptedTimeOut = record.get("timeoutMillis").toInt(10)
        //                             val acceptedQLimit = record.get("q-limit").toLong(10)
        //                             val descriptor = TptpProblemFileDescriptor(domain, form, number, version, size)
        //                             // TODO catch exception here
        //                             classifyFormulas(
        //                                     TptpFofParser.parseFile(
        //                                             TptpFileFetcher.findProblemFile(descriptor))
        //                             ).let { (hyps, targets) ->
        //                                 val hypothesis = createConjunct(hyps)
        //                                 targets.forEach { target ->
        //                                     FolFormulaTableauProver(
        //                                             FolUnificationClosingStrategy(),
        //                                             FolStepStrategy(acceptedQLimit + 1)
        //                                     ).also { prover ->
        //                                         clearInternTables()
        //                                         val timer = fetchTimer(descriptor, "problem")
        //                                         println("Quick test for ${descriptor}.")
        //                                         timer.record {
        //                                             limitedTimeProve(
        //                                                     prover,
        //                                                     hypothesis?.let {
        //                                                         Implies(it, target)
        //                                                     } ?: target,
        //                                                     millis)
        //                                         }
        //                                         writeCsvLine(resultsFile, descriptor, timer, millis, DEFAULT_Q_LIMIT)
        //                                     }
        //                                 }
        //                             }
        //                         }
        //                 }
        //             }
        //     }

    }
}

fun createResults(
    fileName: String,
    domains: List<TptpDomain>,
    forms: List<TptpFormulaForm>,
    qLimit: Long,
    vararg excludes: TptpProblemFileDescriptor
) {

    // tptpWriteResultsFolder.resolve(fileName)
    //     .toFile()
    //     .outputStream()
    //     .bufferedWriter()
    //     .use { resultsFile ->
    //         writeCsvHeader(resultsFile)
    //         TptpFileFetcher.problemFileFilter(domains, forms, *excludes).forEach { descriptor ->
    //             val path = TptpFileFetcher.findProblemFolder(descriptor.domain).resolve(descriptor.toFileName())
    //             classifyFormulas(TptpFofParser.parseFile(path)).let { (hyps, targets) ->
    //                 val hypothesis = createConjunct(hyps)
    //                 targets.forEach { target ->
    //                     FolFormulaTableauProver(
    //                             FolUnificationClosingStrategy(),
    //                             FolStepStrategy(qLimit)
    //                     ).also {
    //                         proveAndWrite(
    //                                 descriptor,
    //                                 hypothesis?.let {
    //                                     Implies(it, target)
    //                                 } ?: target,
    //                                 resultsFile,
    //                                 it)
    //                     }
    //                 }
    //             }
    //         }
    //     }
}

fun createConjunct(
    hyps: MutableList<Formula>
): Formula? {
    var hypothesis = null as Formula?
    if (hyps.isNotEmpty()) {
        hypothesis = hyps.toTypedArray().let {
            if (it.size > 1) {
                And(*it)
            } else {
                it[0]
            }
        }
    }
    return hypothesis
}

fun classifyFormulas(
    tptpFile: List<FolAnnotatedFormula>
): Pair<MutableList<Formula>, MutableList<Formula>> {
    return tptpFile.fold(
        mutableListOf<Formula>() to mutableListOf<Formula>()
    ) { (hyps, targets), input ->
        input.let { annotated ->
            when (annotated.formulaRole) {
                FormulaRole.axiom,
                FormulaRole.hypothesis,
                FormulaRole.assumption,
                FormulaRole.definition,
                FormulaRole.theorem,
                FormulaRole.lemma              -> {
                    hyps.add(annotated.formula)
                }

                FormulaRole.conjecture         -> {
                    targets.add(annotated.formula)
                }
                FormulaRole.negated_conjecture -> {
                    targets.add(Not(annotated.formula))
                }

                FormulaRole.corollary,
                FormulaRole.fi_domain,
                FormulaRole.fi_functors,
                FormulaRole.fi_predicates,
                FormulaRole.plain,
                FormulaRole.type               -> {
                    error("Don't know what to do with ${annotated.formulaRole}.")
                }
                FormulaRole.unknown            -> {
                    // do nothing
                    error("Unknown role found.")
                }
            }
        }
        hyps to targets
    }
}

object CsvHelper {

    val HEADER = "domain,number,version,form,size,millis,timeoutMillis,q-limit,status,message"

    fun writeCsvHeader(o: BufferedWriter) {
        o.write(HEADER)
        o.newLine()
    }

    fun writeCsvLine(
        o: BufferedWriter,
        descriptor: TptpProblemFileDescriptor,
        timer: Timer,
        timeOut: Long,
        qLimit: Long
    ) {
        o.write(
            "${descriptor.domain
            },${descriptor.number
            },${descriptor.version
            },${descriptor.form
            },${descriptor.size
            },${timer.max(TimeUnit.MILLISECONDS)
            },${timeOut
            },${qLimit
            },${result.get().name
            },${result.get().let {
                if (it is Error)
                    it.message
                else
                    ""
            }}"
        )
        o.newLine()
    }
}

fun clearInternTables() {
    Predicate.PredicateConstructor.clear()
    Function.FunctionConstructor.clear()
}

fun proveAndWrite(
    descriptor: TptpProblemFileDescriptor,
    formula: Formula,
    resultsFile: BufferedWriter,
    harness: FolTableauHarness
) {
    clearInternTables()
    println("Quick test for ${descriptor}.")
    val timer = ProblemMeterRegistry.fetchTimer(descriptor)
    timer.record { runBlocking { harness.prove(formula) } }
    CsvHelper.writeCsvLine(resultsFile, descriptor, timer, millis, harness.prover.stepStrategy.qLimit)
}
