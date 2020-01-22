package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.tableau.closer.SuccessfulTableauProofIndicator
import com.benjishults.bitnots.tableau.closer.UnifyingProgressIndicator
import com.benjishults.bitnots.tableau.strategy.FolStepStrategy
import com.benjishults.bitnots.tableau.strategy.FolUnificationClosingStrategy
import com.benjishults.bitnots.tableauProver.FolFormulaTableauProver
import com.benjishults.bitnots.test.limitedTimeProve
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRole
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import com.benjishults.bitnots.tptp.parser.TptpFofParser
import com.benjishults.bitnots.util.meter.NoOpPushMeterRegistry
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.Timer
import io.micrometer.core.instrument.step.StepRegistryConfig
import org.junit.Ignore
import org.junit.Test
import java.io.BufferedWriter
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class TptpSynTest {

    val testResourcesFolder = Paths.get(System.getProperty("user.dir"), "src", "test", "resources")

    val qLimit: Int = 3
    val millis = 1000L
    val registry = NoOpPushMeterRegistry(object : StepRegistryConfig {

        override fun get(key: String): String? {
            return null
        }

        override fun prefix(): String = ""

    }, Clock.SYSTEM);

    val toSucceed = listOf(

            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 0, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 41, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 73, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 79, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 359, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 360, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 363, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 369, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 381, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 387, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 388, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 394, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 395, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 404, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 405, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 410, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 416, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 721, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 727, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 915, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 928, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 945, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 952, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 953, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 955, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 956, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 958, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 962, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 964, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 972, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 973, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 974, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 975, 1, -1),
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 986, 1, 0)
    )

    @Test
    // @Ignore
    fun testSynSyoFol() {
        toSucceed.forEach { (domain, form, number, version, size) ->
            classifyFormulas(TptpFofParser.parseFile(
                    TptpFileFetcher.findProblemFile(
                            domain,
                            form,
                            number,
                            version,
                            size))
            ).let { (hyps, targets) ->
                val hypothesis = createConjunct(hyps)
                targets.forEach { target ->
                    FolFormulaTableauProver(

                            FolUnificationClosingStrategy({ UnifyingProgressIndicator(it) }),
                            FolStepStrategy { sf, n -> FolTableauNode(mutableListOf(sf), n) }
                    ).also { prover ->
                        clearInternTables()
                        assert(
                                limitedTimeProve(
                                        prover,
                                        hypothesis?.let {
                                            Implies(it, target)
                                        } ?: target,
                                        millis
                                ) is SuccessfulTableauProofIndicator)
                    }
                }
            }
        }
    }

    @Test
    @Ignore
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
                        listOf(TptpFormulaForm.FOF),
                        TptpProblemFileDescriptor(
                                domain = TptpDomain.SYN,
                                form = TptpFormulaForm.FOF,
                                number = 361,
                                version = 1,
                                size = -1)
                ).forEach {
                    val path = TptpFileFetcher.findProblemFolder(it.domain).resolve(it.toFileName())
                    classifyFormulas(TptpFofParser.parseFile(path)).let { (hyps, targets) ->
                        val hypothesis = createConjunct(hyps)
                        targets.forEach { target ->
                            FolFormulaTableauProver(

                                    FolUnificationClosingStrategy { UnifyingProgressIndicator(it) },
                                    FolStepStrategy(qLimit) { sf, n -> FolTableauNode(mutableListOf(sf), n) }
                            ).also { prover ->
                                clearInternTables()
                                println("Quick test for ${path}.")
                                val timer = registry.timer("problem",
                                                           "domain", it.domain.name.toLowerCase(),
                                                           "form", it.form.name.toLowerCase(),
                                                           "number", it.number.toString(),
                                                           "version", it.version.toString(),
                                                           "size", it.size.toString())
                                when (timer.record(Supplier<ProofProgressIndicator> {
                                    limitedTimeProve(prover, hypothesis?.let {
                                        Implies(it, target)
                                    } ?: target, millis)
                                })) {
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

    private fun createConjunct(
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

    private fun classifyFormulas(
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

    private fun provePropWithHyps(path: Path, hyps: Int = 0) {
        proveWithHyps(path, hyps, { l -> PropositionalTableauNode(l) }) { PropositionalTableau(it) }
    }

    private fun proveFofWithHyps(path: Path, hyps: Int = 0) {
        proveWithHyps(path, hyps, { l -> FolTableauNode(l) }) { FolTableau(it) }
    }

    private fun <N : TableauNode<N>> proveWithHyps(path: Path, hyps: Int,
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

    private fun writeCsvHeader(o: BufferedWriter) {
        o.write("domain,number,version,form,size,millis,timeoutMillis,q-limit,status")
        o.newLine()
    }

    private fun writeCsvLine(
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

    private fun clearInternTables() {
        Predicate.PredicateConstructor.clear()
        Function.FunctionConstructor.clear()
    }

}
