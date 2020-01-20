package com.benjishults.bitnots.test

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.FolTableauNode
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.PropositionalTableauNode
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.tableau.closer.UnifyingClosedIndicator
import com.benjishults.bitnots.tableau.strategy.FolStepStrategy
import com.benjishults.bitnots.tableau.strategy.FolUnificationClosingStrategy
import com.benjishults.bitnots.tableauProver.FolFormulaTableauProver
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRole
import com.benjishults.bitnots.tptp.TptpProperties
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import com.benjishults.bitnots.tptp.parser.TptpFofParser
import com.benjishults.bitnots.util.meter.NoOpPushMeterRegistry
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.Timer
import io.micrometer.core.instrument.step.StepRegistryConfig
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.BufferedWriter
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

val result: AtomicReference<Result> = AtomicReference(Error(""))

val tptpReadResultsFolder = Paths.get(TptpProperties.getReadResultsFolderName())
val tptpWriteResultsFolder = Paths.get(TptpProperties.getWriteResultsFolderName())

val qLimit: Int = 3
val millis = 1000L
val registry = NoOpPushMeterRegistry(object : StepRegistryConfig {
    override fun get(key: String): String? {
        return null
    }

    override fun prefix(): String = ""
}, Clock.SYSTEM);

val HEADER = "domain,number,version,form,size,millis,timeoutMillis,q-limit,status,message"

fun doAsWellAsAccepted() {
    tptpReadResultsFolder.resolve("results.csv")
        .toFile()
        .reader()
        .use { acceptedResultsFile ->
            CSVParser(
                    acceptedResultsFile,
                    CSVFormat.DEFAULT.withHeader(*HEADER.split(",").toTypedArray())
            ).use { parser ->
                // if it succeeded last time
                parser.filter { it.get("status") == "success" }
                    .forEach { record ->
                        val domain = TptpDomain.valueOf(record.get("domain"))
                        val form = TptpFormulaForm.valueOf(record.get("form"))
                        val number = record.get("number").toInt(10)
                        val version = record.get("version").toInt(10)
                        val size = record.get("size").toInt(10)
                        val acceptedMillis = record.get("millis").toDouble()
                        val acceptedTimeOut = record.get("timeoutMillis").toInt(10)
                        val acceptedQLimit = record.get("q-limit").toInt(10)
                        val descriptor = TptpProblemFileDescriptor(domain, form, number, version, size)
                        classifyFormulas(
                                TptpFofParser.parseFile(
                                        TptpFileFetcher.findProblemFile(descriptor))
                        ).let { (hyps, targets) ->
                            val hypothesis = createConjunct(hyps)
                            targets.forEach { target ->
                                FolFormulaTableauProver(
                                        hypothesis?.let {
                                            Implies(it, target)
                                        } ?: target,
                                        FolUnificationClosingStrategy { UnifyingClosedIndicator(it) },
                                        FolStepStrategy(acceptedQLimit) { sf, n ->
                                            FolTableauNode(mutableListOf(sf), n)
                                        }
                                ).also { prover ->
                                    clearInternTables()
                                    val timer = registry.timer("problem",
                                                               "domain", descriptor.domain.name.toLowerCase(),
                                                               "form", descriptor.form.name.toLowerCase(),
                                                               "number", descriptor.number.toString(),
                                                               "version", descriptor.version.toString(),
                                                               "size", descriptor.size.toString())

                                    timer.record {
                                        limitedTimeProve(prover, acceptedMillis.toLong() + 1)
                                    }
                                    assert(result.get() === Proved)
                                    val max = timer.max(TimeUnit.MILLISECONDS)
                                    if (1.0 - (Math.abs(max - acceptedMillis) / acceptedMillis) > .1)
                                        println("This run ($max) was about as fast as the previous ($acceptedMillis) for $descriptor with q-limit $acceptedQLimit")
                                    else
                                        println("This run ($max) was significantly slower than the previous ($acceptedMillis) for $descriptor with q-limit $acceptedQLimit")
                                }
                            }
                        }
                    }
            }
        }
}

fun tryHigherQLimit() {
    tptpWriteResultsFolder.resolve("results${Instant.now().toEpochMilli()}.csv")
        .toFile()
        .outputStream()
        .bufferedWriter()
        .use { resultsFile ->
            writeCsvHeader(resultsFile)

            tptpReadResultsFolder.resolve("results.csv")
                .toFile()
                .reader()
                .use { acceptedResultsFile ->
                    CSVParser(
                            acceptedResultsFile,
                            CSVFormat.DEFAULT.withHeader(*HEADER.split(",").toTypedArray())
                    ).use { parser ->
                        // if it succeeded last time
                        parser.filter { it.get("status") == "fail" }
                            .filter { it.get("millis").toDouble() < (it.get("timeoutMillis").toDouble() * 0.75) }
                            .forEach { record ->
                                val domain = TptpDomain.valueOf(record.get("domain"))
                                val form = TptpFormulaForm.valueOf(record.get("form"))
                                val number = record.get("number").toInt(10)
                                val version = record.get("version").toInt(10)
                                val size = record.get("size").toInt(10)
                                val acceptedMillis = record.get("millis").toDouble()
                                val acceptedTimeOut = record.get("timeoutMillis").toInt(10)
                                val acceptedQLimit = record.get("q-limit").toInt(10)
                                val descriptor = TptpProblemFileDescriptor(domain, form, number, version, size)
                                classifyFormulas(
                                        TptpFofParser.parseFile(
                                                TptpFileFetcher.findProblemFile(descriptor))
                                ).let { (hyps, targets) ->
                                    val hypothesis = createConjunct(hyps)
                                    targets.forEach { target ->
                                        FolFormulaTableauProver(
                                                hypothesis?.let {
                                                    Implies(it, target)
                                                } ?: target,
                                                FolUnificationClosingStrategy { UnifyingClosedIndicator(it) },
                                                FolStepStrategy(acceptedQLimit + 1) { sf, n ->
                                                    FolTableauNode(mutableListOf(sf), n)
                                                }
                                        ).also { prover ->
                                            clearInternTables()
                                            val timer = registry.timer("problem",
                                                                       "domain", descriptor.domain.name.toLowerCase(),
                                                                       "form", descriptor.form.name.toLowerCase(),
                                                                       "number", descriptor.number.toString(),
                                                                       "version", descriptor.version.toString(),
                                                                       "size", descriptor.size.toString())
                                            println("Quick test for ${descriptor}.")
                                            timer.record { limitedTimeProve(prover, millis) }
                                            writeCsvLine(resultsFile, descriptor, timer, millis, qLimit)
                                        }
                                    }
                                }
                            }
                    }
                }
        }

}

fun createResultsForSynFof() {
    createResults(
            listOf(TptpDomain.SYN),
            listOf(TptpFormulaForm.FOF)/*,
                    TptpProblemFileDescriptor(
                            domain = TptpDomain.SYN,
                            form = TptpFormulaForm.FOF,
                            number = 361,
                            version = 1,
                            size = -1)*/
    )
}

fun createResultsForAllFof() {
    createResults(
            listOf(*TptpDomain.values()),
            listOf(TptpFormulaForm.FOF),
            // stack overflow on parsing file
            TptpProblemFileDescriptor(TptpDomain.LCL, TptpFormulaForm.FOF, 680, 1, 15),
            // stack overflow on parsing file
            TptpProblemFileDescriptor(TptpDomain.LCL, TptpFormulaForm.FOF, 680, 1, 20),
            // didn't even try
            TptpProblemFileDescriptor(TptpDomain.LCL, TptpFormulaForm.FOF, 681, 1, 20),
            // didn't even try
            TptpProblemFileDescriptor(TptpDomain.LCL, TptpFormulaForm.FOF, 684, 1, 20),
            // didn't even try
            TptpProblemFileDescriptor(TptpDomain.LCL, TptpFormulaForm.FOF, 685, 1, 20),
            // Unexpected character at beginning of FOF '+' at line 30 of /usr/local/share/tptp/Problems/LCL/LCL882+1.p.
            TptpProblemFileDescriptor(TptpDomain.LCL, TptpFormulaForm.FOF, 882, 1)
    )
}

fun createResults(domains: List<TptpDomain>, forms: List<TptpFormulaForm>, vararg excludes: TptpProblemFileDescriptor) {

    tptpWriteResultsFolder.resolve("results${Instant.now().toEpochMilli()}.csv")
        .toFile()
        .outputStream()
        .bufferedWriter()
        .use { resultsFile ->
            writeCsvHeader(resultsFile)
            TptpFileFetcher.problemFileFilter(domains, forms, *excludes).forEach { descriptor ->
                val path = TptpFileFetcher.findProblemFolder(descriptor.domain).resolve(descriptor.toFileName())
                classifyFormulas(TptpFofParser.parseFile(path)).let { (hyps, targets) ->
                    val hypothesis = createConjunct(hyps)
                    targets.forEach { target ->
                        FolFormulaTableauProver(
                                hypothesis?.let {
                                    Implies(it, target)
                                } ?: target,
                                FolUnificationClosingStrategy { UnifyingClosedIndicator(it) },
                                FolStepStrategy(qLimit) { sf, n -> FolTableauNode(mutableListOf(sf), n) }
                        ).also {
                            proveAndWrite(descriptor, resultsFile, it)
                        }
                    }
                }
            }
        }
}

fun takesTooLong() {

    try {
        val path = TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 7, 1, 14)

        TptpFofParser.parseFile(path).let { tptp ->
            PropositionalTableau(
                    PropositionalTableauNode(mutableListOf<SignedFormula<Formula<*>>>(
                            (tptp.last() as FolAnnotatedFormula).formula.createSignedFormula()),
                                             null)).also { tableau ->

                // while (true) {
                //     if (tableau.findCloser().isDone())
                //         break
                //     if (!tableau.step())
                //         Assert.fail("Failed to prove it with unlimited steps.")
                // }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }
}

fun createConjunct(
        hyps: MutableList<Formula<*>>): Formula<*>? {
    var hypothesis = null as Formula<*>?
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
        tptpFile: List<FolAnnotatedFormula>): Pair<MutableList<Formula<*>>, MutableList<Formula<*>>> {
    return tptpFile.fold(
            mutableListOf<Formula<*>>() to mutableListOf<Formula<*>>()) { (hyps, targets), input ->
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
                    // error("Unknown role found.")
                }
            }
        }
        hyps to targets
    }
}

fun prove(tableauProverFol: FolFormulaTableauProver): Unit {
    try {
        while (!Thread.interrupted()) {
            if (tableauProverFol.searchForFinisher().isDone()) {
                result.set(Proved)
                return
            } else if (Thread.interrupted()) {
                break
            }
            if (!tableauProverFol.step()) {
                result.set(Failed)
                return
            }
        }
        result.set(TimeOut)
    } catch (t: Throwable) {
        result.set(Error(t.message ?: ""))
    }
}

class ResultThread(
        val tableauProverFol: FolFormulaTableauProver
) : Thread() {

    override fun run() {
        prove(tableauProverFol)
    }
}

fun limitedTimeProve(proof: FolFormulaTableauProver, millis: Long): Unit {
    ResultThread(proof).let {
        try {
            it.start()
        } finally {
            it.join(millis)
            it.interrupt()
            it.join()
        }
    }
}

fun provePropWithHyps(path: Path, hyps: Int = 0) {
    proveWithHyps(path, hyps, { l -> PropositionalTableauNode(l) }) { PropositionalTableau(it) }
}

fun proveFofWithHyps(path: Path, hyps: Int = 0) {
    proveWithHyps(path, hyps, { l -> FolTableauNode(l) }) { FolTableau(it) }
}

fun <N : TableauNode<N>> proveWithHyps(
        path: Path,
        hyps: Int,
        nodeFactory: (MutableList<SignedFormula<Formula<*>>>) -> N,
        tabFactory: (N) -> Tableau<N>
) {
    try {
        println("Working on ${path}.")
        TptpFofParser.parseFile(path).let { tptp ->
            tabFactory(
                    nodeFactory(mutableListOf<SignedFormula<Formula<*>>>(
                            if (hyps > 0) {
                                Implies(
                                        tptp.dropLast(hyps).map {
                                            (it as FolAnnotatedFormula).formula
                                        }.toTypedArray().let {
                                            if (it.size > 1) {
                                                And(*it)
                                            } else {
                                                it[0]
                                            }
                                        },
                                        (tptp.last() as FolAnnotatedFormula).formula).createSignedFormula()
                            } else {
                                (tptp.last() as FolAnnotatedFormula).formula.createSignedFormula()
                            }))).also { tableau ->
                //                    var steps = 0
                // while (true) {
                //     if (tableau.findCloser().isDone())
                //         break
                //     if (!tableau.step())
                //         Assert.fail("Failed to prove it with unlimited steps.")
                // }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    } finally {
        clearInternTables()
    }
}

fun writeCsvHeader(o: BufferedWriter) {
    o.write(HEADER)
    o.newLine()
}

fun writeCsvLine(
        o: BufferedWriter,
        descriptor: TptpProblemFileDescriptor,
        timer: Timer,
        timeOut: Long,
        qLimit: Int) {
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
            }}")
    o.newLine()
}

fun clearInternTables() {
    Predicate.PredicateConstructor.clear()
    Function.FunctionConstructor.clear()
}

fun proveAndWrite(
        descriptor: TptpProblemFileDescriptor,
        resultsFile: BufferedWriter,
        prover: FolFormulaTableauProver) {
    clearInternTables()
    println("Quick test for ${descriptor}.")
    val timer = registry.timer("problem",
                               "domain", descriptor.domain.name.toLowerCase(),
                               "form", descriptor.form.name.toLowerCase(),
                               "number", descriptor.number.toString(),
                               "version", descriptor.version.toString(),
                               "size", descriptor.size.toString())
    timer.record { limitedTimeProve(prover, millis) }
    writeCsvLine(resultsFile, descriptor, timer, millis, qLimit)
}
