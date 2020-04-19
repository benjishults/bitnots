package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRole
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFof
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import com.benjishults.bitnots.tptp.parser.TptpFofParser
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("localTptp")
class TptpSynTest {

    // private val testResourcesFolder: Path = Paths.get(System.getProperty("user.dir"), "src", "test", "resources")

    private val millis = 1000L

    private val toSucceed = listOf(

        // TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 0, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 41, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 73, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 79, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 359, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 360, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 363, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 369, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 381, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 387, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 388, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 394, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 395, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 404, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 405, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 410, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 416, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 721, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 727, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 915, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 928, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 945, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 952, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 953, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 955, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 956, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 958, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 962, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 964, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 972, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 973, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 974, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 975, 1, -1),
        TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 986, 1, 0)
    )

    @Test
    fun testSynSyoFol() {
        toSucceed.forEach { descriptor ->
            val (domain, form, number, version, size) = descriptor
            classifyFormulas(
                TptpFofParser.parseFile(
                    TptpFileFetcher.findProblemFile(
                        domain,
                        form,
                        number,
                        version,
                        size
                    )
                )
            ).let { (hyps, targets) ->
                val hypothesis = createConjunct(hyps)
                targets.forEach { target ->
                    FolTableauHarness(limitMillis = millis).let { prover ->
                        clearInternTables()
                        Assertions.assertTrue(runBlocking {
                            prover.prove(
                                hypothesis?.let {
                                    Implies(it, target)
                                } ?: target
                            ).indicator.isDone()
                        })
                    }
                }
            }
        }
    }

    private fun createConjunct(
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

    private fun classifyFormulas(
        tptpFile: List<FolAnnotatedFormula>
    ): Pair<MutableList<Formula>, MutableList<Formula>> {
        return tptpFile.fold(
            mutableListOf<Formula>() to mutableListOf()
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
                        error("Unknown role found.")
                    }
                }
            }
            hyps to targets
        }
    }

    private fun clearInternTables() {
        Predicate.PredicateConstructor.clear()
        Function.FunctionConstructor.clear()
    }

}
