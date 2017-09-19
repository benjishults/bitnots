package com.benjishults.bitnots.engine

import com.benjishults.bitnots.engine.proof.FolTableau
import com.benjishults.bitnots.engine.proof.FolTableauNode
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.createSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.And
import com.benjishults.bitnots.model.formulas.propositional.Implies
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.parser.FofAnnotatedFormula
import com.benjishults.bitnots.tptp.parser.TptpParser
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class TopologyTests {

    @Test
//    @Ignore
    fun testFofGroups() {
        try {
            val path = TptpFileFetcher.findProblemFile(TptpDomain.TOP, TptpFormulaForm.FOF, 22, 1)

            TptpParser.parseFile(path).let { tptp ->
                FolTableau(FolTableauNode(mutableListOf<SignedFormula<Formula<*>>>(
                        Implies(And(*tptp.inputs.dropLast(1).map {
                            (it as FofAnnotatedFormula).formula
                        }.toTypedArray()),
                                (tptp.inputs.last() as FofAnnotatedFormula).formula).createSignedFormula()),
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

fun main(args: Array<String>) {
    System.setProperty("config", "/home/bshults/repos/benjishults/bitnots/config")
    TopologyTests().testFofGroups()
}
