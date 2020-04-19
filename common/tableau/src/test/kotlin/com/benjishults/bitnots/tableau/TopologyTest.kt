package com.benjishults.bitnots.tableau

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TopologyTest {

    @Test
    @Disabled
    fun testFofGroups() {
//        try {
//            val path = TptpFileFetcher.findProblemFile(TptpDomain.TOP, TptpFof, 22, 1)
//
//            TptpParser.parseFile(path).let { tptp ->
//                FolTableau(FolTableauNode(
//                        mutableListOf<SignedFormula<Formula>>(
//                                Implies(And(*tptp.inputs.dropLast(1).map {
//                                    (it as FofAnnotatedFormula).formula
//                                }.toTypedArray()),
//                                        (tptp.inputs.last() as FofAnnotatedFormula).formula).createSignedFormula()))).also { tableau ->
//                    while (true) {
//                        if (tableau.isClosed())
//                            break
//                        if (!tableau.step())
//                            Assertions.fail("Failed to prove it with unlimited steps.")
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw e
//        }
    }

}
