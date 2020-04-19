package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.theory.formula.FormulaRole
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFof
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("localTptp")
class ProblemBuilderTest {

    @Test
    fun problemBuilderTest() {
        try { // TOP020+1.p hausdorff problem
            val path = TptpFileFetcher.findProblemFile(TptpDomain.TOP, TptpFof, 20, 1)
            TptpFofParser.parseFile(path).let {formulas->
                Assertions.assertEquals(9, formulas.size)
                Assertions.assertTrue(formulas.any { it.formulaRole == FormulaRole.conjecture })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

}
