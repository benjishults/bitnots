package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.theory.formula.FormulaRole
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import org.junit.Assert
import org.junit.Test

class ProblemBuilderTest {

    @Test
    fun problemBuilderTest() {
        try { // TOP020+1.p hausdorff problem
            val path = TptpFileFetcher.findProblemFile(TptpDomain.TOP, TptpFormulaForm.FOF, 20, 1)
            TptpFofParser.parseFile(path).let {formulas->
                Assert.assertEquals(9, formulas.size)
                Assert.assertTrue(formulas.any { it.formulaRole == FormulaRole.conjecture })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

}
