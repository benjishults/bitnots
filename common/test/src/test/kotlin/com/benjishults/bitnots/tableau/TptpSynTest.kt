package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.formulas.propositional.Not
import com.benjishults.bitnots.model.terms.Function
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
import org.junit.Assert
import org.junit.Test

class TptpSynTest {

    // private val testResourcesFolder: Path = Paths.get(System.getProperty("user.dir"), "src", "test", "resources")

    private val millis = 1000L

    private val toSucceed = listOf(

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
    fun testSynSyoFol() {
        toSucceed.forEach { descriptor ->
            val (domain, form, number, version, size) = descriptor
            println(descriptor)
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
                            FolUnificationClosingStrategy(),
                            FolStepStrategy()
                    ).let { prover ->
                        clearInternTables()
                        Assert.assertTrue(
                                limitedTimeProve(
                                        prover,
                                        hypothesis?.let {
                                            Implies(it, target)
                                        } ?: target,
                                        millis
                                ).isDone())
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
                mutableListOf<Formula<*>>() to mutableListOf()) { (hyps, targets), input ->
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
