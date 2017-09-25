package com.benjishults.bitnots.engine

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

class PlaistedTest {

//    @Test
//    @Ignore
//    fun testFofPlaisted() {
//        try {
//            val path = TptpFileFetcher.findProblemFile(TptpDomain.PLA, TptpFormulaForm.FOF, 34, 1)
//
//            TptpParser.parseFile(path).let { tptp ->
//                FolTableau(FolTableauNode(ArrayList<SignedFormula<Formula<*>>>().also {
//                    it.add((tptp.inputs.first() as FofAnnotatedFormula).formula.createSignedFormula())
//                }, null)).also { tableau ->
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
//    }

}
