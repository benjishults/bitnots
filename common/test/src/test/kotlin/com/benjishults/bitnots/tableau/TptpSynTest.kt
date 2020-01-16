package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.tableau.closer.UnifyingClosedIndicator
import com.benjishults.bitnots.tableau.strategy.FolStepStrategy
import com.benjishults.bitnots.tableau.strategy.FolUnificationClosingStrategy
import com.benjishults.bitnots.tableauProver.FolFormulaTableauProver
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRole
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.parser.TptpFofParser
import org.junit.Ignore
import org.junit.Test
import java.nio.file.Path
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class TptpSynTest {

    val millis = 1000L

    private fun clearInternTables() {
        Predicate.PredicateConstructor.clear()
        Function.FunctionConstructor.clear()
    }

    val toSucceed = listOf(
            Triple(0, 1, -1),
            Triple(41, 1, -1),
            Triple(73, 1, -1),
            Triple(79, 1, -1),
            Triple(359, 1, -1),
            Triple(360, 1, -1),
            Triple(363, 1, -1),
            Triple(369, 1, -1),
            Triple(381, 1, -1),
            Triple(387, 1, -1),
            Triple(388, 1, -1),
            Triple(394, 1, -1),
            Triple(395, 1, -1),
            Triple(404, 1, -1),
            Triple(405, 1, -1),
            Triple(410, 1, -1),
            Triple(416, 1, -1),
            Triple(721, 1, -1),
            Triple(727, 1, -1),
            Triple(915, 1, -1),
            Triple(928, 1, -1),
            Triple(945, 1, -1),
            Triple(952, 1, -1),
            Triple(953, 1, -1),
            Triple(955, 1, -1),
            Triple(956, 1, -1),
            Triple(958, 1, -1),
            Triple(962, 1, -1),
            Triple(964, 1, -1),
            Triple(972, 1, -1),
            Triple(973, 1, -1),
            Triple(974, 1, -1),
            Triple(975, 1, -1),
            Triple(986, 1, 0)
    )

    @Test
    fun testSynSyoFol() {
        toSucceed.forEach { (number, version, size) ->
            classifyFormulas(TptpFofParser.parseFile(
                    TptpFileFetcher.findProblemFile(
                            TptpDomain.SYN,
                            TptpFormulaForm.FOF,
                            number,
                            version,
                            size))
            ).let { (hyps, targets) ->
                val hypothesis = createConjunct(hyps)
                targets.forEach { target ->
                    FolFormulaTableauProver(
                            hypothesis?.let {
                                Implies(it, target)
                            } ?: target,
                            FolUnificationClosingStrategy({ UnifyingClosedIndicator(it) }),
                            FolStepStrategy { sf, n -> FolTableauNode(mutableListOf(sf), n) }
                    ).also { prover ->
                        clearInternTables()
                        assert(limitedTimeProve(prover, millis) === Result.proved)
                    }
                }
            }
        }
    }

    @Test
    @Ignore
    fun testAllSynSyoFol() {
        val failures = mutableListOf<Path>()
        val successes = mutableListOf<Path>()
        val timeouts = mutableListOf<Path>()

        TptpFileFetcher.findAll(TptpDomain.SYN, TptpFormulaForm.FOF).forEach { path ->
            classifyFormulas(TptpFofParser.parseFile(path)).let { (hyps, targets) ->
                val hypothesis = createConjunct(hyps)
                targets.forEach { target ->
                    FolFormulaTableauProver(
                            hypothesis?.let {
                                Implies(it, target)
                            } ?: target,
                            FolUnificationClosingStrategy { UnifyingClosedIndicator(it) },
                            FolStepStrategy { sf, n -> FolTableauNode(mutableListOf(sf), n) }
                    ).also { prover ->
                        clearInternTables()
                        print("Quick test for ${path}.")
                        when (limitedTimeProve(prover, millis)) {
                            Result.failed  -> {
                                failures.add(path)
                                println(" failed")
                            }
                            Result.proved  -> {
                                successes.add(path)
                                println(" succeeded")
                            }
                            Result.timeout -> {
                                timeouts.add(path)
                                println(" timed out")
                            }
                        }
                    }
                }
            }
        }
        println("Failures: ")
        failures.forEach { println(it) }
        println("Proved: ")
        successes.forEach {
            println(it)
        }
        println("Timeout after ${millis} milliseconds: ")
        timeouts.forEach { println(it) }
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

    private fun limitedTimeProve(proof: FolFormulaTableauProver, millis: Long): Result =
            ResultThread(proof).let {
                try {
                    it.start()
                    it.get(millis, TimeUnit.MILLISECONDS)
                } finally {
                    it.join()
                }
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
            while (!Thread.interrupted()) {
                if (tableauProverFol.searchForFinisher().isDone()) {
                    result = Result.proved
                    return
                } else if (Thread.interrupted()) {
                    result = Result.timeout
                    return
                }
                if (!tableauProverFol.step()) {
                    result = Result.failed
                    return
                }
            }
            result = Result.timeout
        }
    }

    @Test
    @Ignore
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

    @Test
    @Ignore
    fun equalityProblems() {

    }

    @Test
    fun shouldBeAbleToGet() {
    }

    @Test
    @Ignore
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

    @Test
    @Ignore
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

}
