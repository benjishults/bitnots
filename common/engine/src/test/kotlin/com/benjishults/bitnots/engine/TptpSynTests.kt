package com.benjishults.bitnots.engine

import com.benjishults.bitnots.engine.proof.PropositionalTableau
import com.benjishults.bitnots.engine.proof.PropositionalTableauNode
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.parser.FofAnnotatedFormula
import com.benjishults.bitnots.tptp.parser.TptpParser
import org.junit.Assert
import org.junit.Test
import org.junit.Ignore

class TptpSynTests {

    @Test
    fun testSynProblems() {
        try {
            val path = TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 1, 1)

            TptpParser.parseFile(path).let { tptp ->
                PropositionalTableau(
                        PropositionalTableauNode(mutableListOf<SignedFormula<Formula<*>>>(
                                (tptp.inputs.last() as FofAnnotatedFormula).formula.createSignedFormula()),
                                null)).also { tableau ->
                    while (true) {
                        if (tableau.isClosed())
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
        try {
            val path = TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 41, 1)
                    
                    TptpParser.parseFile(path).let { tptp ->
                    PropositionalTableau(
                            PropositionalTableauNode(mutableListOf<SignedFormula<Formula<*>>>(
                                    (tptp.inputs.last() as FofAnnotatedFormula).formula.createSignedFormula()),
                                    null)).also { tableau ->
                                    while (true) {
                                        if (tableau.isClosed())
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
                        if (tableau.isClosed())
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