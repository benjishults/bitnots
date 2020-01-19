@file:CompilerOpts("-jvm-target 1.8 -Xjsr305=strict")
@file:DependsOn("com.benjishults.bitnots:util:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:language:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:theory:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:inference:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:tableau:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:prover:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:tableau-prover:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:parser:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:tptp-parser:0.0.1-SNAPSHOT")
@file:DependsOn("io.micrometer:micrometer-core:1.3.2")
@file:DependsOn("org.apache.commons:commons-csv:1.7")
@file:DependsOn("org.apache.commons:commons-csv:1.7")
@file:DependsOn("org.jetbrains.kotlin:kotlin-main-kts:1.3.61")

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
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import com.benjishults.bitnots.tptp.parser.TptpFofParser
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.Timer
import io.micrometer.core.instrument.logging.LoggingMeterRegistry
import io.micrometer.core.instrument.logging.LoggingRegistryConfig
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.jetbrains.kotlin.script.util.DependsOn
import java.io.BufferedWriter
import java.nio.file.Path
import java.nio.file.Paths
// import java.nio.file.Path.of as pathOf
import java.time.Duration
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

System.setProperty("config", "/home/benji/repos/benjishults/bitnots/common/tptp-parser/src/main/resources")

val testResourcesFolder = Paths.get(System.getProperty("user.home"),
                                    "repos",
                                    "benjishults",
                                    "bitnots",
                                    "common",
                                    "test",
                                    "src",
                                    "test",
                                    "resources")

val qLimit: Int = 3
val millis = 1000L
val registry = LoggingMeterRegistry(object : LoggingRegistryConfig {
    override fun get(key: String): String? {
        return null
    }

    override fun step(): Duration {
        return Duration.ofSeconds(1)
    }
}, Clock.SYSTEM);

val HEADER = "domain,number,version,form,size,millis,timeoutMillis,q-limit,status"

fun doAsWellAsAccepted() {
    testResourcesFolder.resolve("accepted")
        .resolve("results.csv")
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

                                    assert(
                                            timer.record(Supplier<Result> {
                                                limitedTimeProve(prover, acceptedMillis.toLong() + 1)
                                            }) === Result.proved)
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

doAsWellAsAccepted()

fun testAllSynSyoFol() {
    testResourcesFolder.resolve("latest")
        .resolve("results.csv")
        .toFile()
        .outputStream()
        .bufferedWriter()
        .use { resultsFile ->
            writeCsvHeader(resultsFile)
            TptpFileFetcher.problemFileFilter(
                    listOf(TptpDomain.SYN),
                    listOf(TptpFormulaForm.FOF)/*,
                    TptpProblemFileDescriptor(
                            domain = TptpDomain.SYN,
                            form = TptpFormulaForm.FOF,
                            number = 361,
                            version = 1,
                            size = -1)*/
            ).forEach {
                val path = TptpFileFetcher.findProblemFolder(it.domain).resolve(it.toFileName())
                classifyFormulas(TptpFofParser.parseFile(path)).let { (hyps, targets) ->
                    val hypothesis = createConjunct(hyps)
                    targets.forEach { target ->
                        FolFormulaTableauProver(
                                hypothesis?.let {
                                    Implies(it, target)
                                } ?: target,
                                FolUnificationClosingStrategy { UnifyingClosedIndicator(it) },
                                FolStepStrategy(qLimit) { sf, n -> FolTableauNode(mutableListOf(sf), n) }
                        ).also { prover ->
                            clearInternTables()
                            print("Quick test for ${path}.")
                            val timer = registry.timer("problem",
                                                       "domain", it.domain.name.toLowerCase(),
                                                       "form", it.form.name.toLowerCase(),
                                                       "number", it.number.toString(),
                                                       "version", it.version.toString(),
                                                       "size", it.size.toString())
                            when (timer.record(Supplier<Result> { limitedTimeProve(prover, millis) })) {
                                Result.failed  -> {
                                    writeCsvLine(resultsFile, it, timer, "fail", millis, qLimit)
                                }
                                Result.proved  -> {
                                    writeCsvLine(resultsFile, it, timer, "success", millis, qLimit)
                                }
                                Result.timeout -> {
                                    writeCsvLine(resultsFile, it, timer, "timeout", millis, qLimit)
                                }
                            }
                        }
                    }
                }
            }
        }
}

fun testSynProblems() {
    provePropWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 1, 1))
    provePropWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 41, 1))
    provePropWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 44, 1), 1)
    provePropWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 45, 1))
    provePropWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 46, 1))
    provePropWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 47, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 48, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 49, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 50, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 51, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 52, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 53, 1))

    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 63, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 64, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 65, 1), 1)

    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 73, 1))

    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 315, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 317, 1))

    // working from the end

    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 981, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 986, 1, 0), 1)
}

fun notWorkingYet() {
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 54, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 55, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 56, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 57, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 58, 1), 1)
    //         TODO seems to require q-limit > 3
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 59, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 60, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 61, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 62, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 66, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 67, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 68, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 69, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 70, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 79, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 81, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 82, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 84, 1))

    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 316, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 318, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 319, 1))
    //         TODO seems to require q-limit > 3
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 320, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 321, 1))
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 322, 1))


    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 986, 1, 1), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 986, 1, 2), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 986, 1, 3), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 986, 1, 4), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 986, 1, 5), 1)
    proveFofWithHyps(TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 986, 1, 6), 1)

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

enum class Result {
    proved,
    failed,
    timeout
}

fun prove(tableauProverFol: FolFormulaTableauProver): Result {
    while (!Thread.interrupted()) {
        if (tableauProverFol.searchForFinisher().isDone()) {
            return Result.proved
        } else if (Thread.interrupted()) {
            return Result.timeout
        }
        if (!tableauProverFol.step()) {
            return Result.failed
        }
    }
    return Result.timeout
}

inner class ResultThread(
        val tableauProverFol: FolFormulaTableauProver,
        var result: Result = Result.failed
) : Thread(), Future<Result> {
    override fun get(): Result {
        join()
        return result
    }

    override fun get(timeout: Long, unit: TimeUnit?): Result {
        join(TimeUnit.MILLISECONDS.convert(timeout, unit))
        if (isAlive) {
            interrupt()
            return Result.timeout
        }
        return result
    }

    override fun isDone(): Boolean {
        return !isAlive
    }

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        if (isDone) {
            return false;
        } else if (mayInterruptIfRunning) {
            interrupt()
        }
        return true
    }

    override fun isCancelled(): Boolean {
        TODO()
    }

    override fun run() {
        result = prove(tableauProverFol)
    }
}

fun limitedTimeProve(proof: FolFormulaTableauProver, millis: Long): Result =
        ResultThread(proof).let {
            try {
                it.start()
                it.get(millis, TimeUnit.MILLISECONDS)
            } finally {
                it.join()
            }
        }

fun provePropWithHyps(path: Path, hyps: Int = 0) {
    proveWithHyps(path, hyps, { l -> PropositionalTableauNode(l) }) { PropositionalTableau(it) }
}

fun proveFofWithHyps(path: Path, hyps: Int = 0) {
    proveWithHyps(path, hyps, { l -> FolTableauNode(l) }) { FolTableau(it) }
}

fun <N : TableauNode<N>> proveWithHyps(path: Path, hyps: Int,
                                       nodeFactory: (MutableList<SignedFormula<Formula<*>>>) -> N,
                                       tabFactory: (N) -> Tableau<N>) {
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
        status: String,
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
            },${status}")
    o.newLine()
}

fun clearInternTables() {
    Predicate.PredicateConstructor.clear()
    Function.FunctionConstructor.clear()
}
