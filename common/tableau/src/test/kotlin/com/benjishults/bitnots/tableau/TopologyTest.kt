package com.benjishults.bitnots.tableau

import org.junit.Ignore
import org.junit.Test

class TopologyTest {

    @Test
    @Ignore
    fun testFofGroups() {
//        try {
//            val path = TptpFileFetcher.findProblemFile(TptpDomain.TOP, TptpFormulaForm.FOF, 22, 1)
//
//            TptpParser.parseFile(path).let { tptp ->
//                FolTableau(FolTableauNode(
//                        mutableListOf<SignedFormula<Formula<*>>>(
//                                Implies(And(*tptp.inputs.dropLast(1).map {
//                                    (it as FofAnnotatedFormula).formula
//                                }.toTypedArray()),
//                                        (tptp.inputs.last() as FofAnnotatedFormula).formula).createSignedFormula()))).also { tableau ->
//                    while (true) {
//                        if (tableau.isClosed())
//                            break
//                        if (!tableau.step())
//                            Assert.fail("Failed to prove it with unlimited steps.")
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw e
//        }
    }

}
