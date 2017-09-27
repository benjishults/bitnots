package com.benjishults.bitnots.engine

import com.benjishults.bitnots.engine.proof.FolTableau
import com.benjishults.bitnots.engine.proof.FolTableauNode
import com.benjishults.bitnots.engine.proof.PropositionalTableau
import com.benjishults.bitnots.engine.proof.PropositionalTableauNode
import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.Predicate
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.model.terms.Function
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.parser.FofAnnotatedFormula
import com.benjishults.bitnots.tptp.parser.TptpParser
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.nio.file.Path

class TptpSynTest {

    private fun clearInternTables() {
        Predicate.PredicateConstructor.clear()
        Function.FunctionConstructor.clear()
    }

    @Test
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

    private fun <N : TableauNode> proveWithHyps(path: Path, hyps: Int, nodeFactory: (MutableList<SignedFormula<Formula<*>>>) -> N, tabFactory: (N) -> Tableau) {
        try {
            println("Working on ${path}.")
            TptpParser.parseFile(path).let { tptp ->
                tabFactory(
                        nodeFactory(mutableListOf<SignedFormula<Formula<*>>>(
                                if (hyps > 0) {
                                    Implies(
                                            tptp.inputs.dropLast(hyps).map {
                                                (it as FofAnnotatedFormula).formula
                                            }.toTypedArray().let {
                                                if (it.size > 1) {
                                                    And(*it)
                                                } else {
                                                    it[0]
                                                }
                                            },
                                            (tptp.inputs.last() as FofAnnotatedFormula).formula).createSignedFormula()
                                } else {
                                    (tptp.inputs.last() as FofAnnotatedFormula).formula.createSignedFormula()
                                }))).also { tableau ->
                    //                    var steps = 0
                    while (true) {
                        if (tableau.findCloser().isCloser())
                            break
                        if (!tableau.step())
                            Assert.fail("Failed to prove it with unlimited steps.")
                    }
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

            TptpParser.parseFile(path).let { tptp ->
                PropositionalTableau(
                        PropositionalTableauNode(mutableListOf<SignedFormula<Formula<*>>>(
                                (tptp.inputs.last() as FofAnnotatedFormula).formula.createSignedFormula()),
                                null)).also { tableau ->
                    while (true) {
                        if (tableau.findCloser().isCloser())
                            break
                        if (!tableau.step())
                            Assert.fail("Failed to prove it with unlimited steps.")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

}
