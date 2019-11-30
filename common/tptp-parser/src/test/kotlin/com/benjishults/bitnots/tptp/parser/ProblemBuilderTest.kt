package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.theory.formula.CnfAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRoles
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.parser.TptpCnfParser
import com.benjishults.bitnots.tptp.parser.TptpFofParser
import org.junit.Assert
import org.junit.Test
import java.nio.file.FileSystems

class ProblemBuilderTest {

    @Test
    fun problemBuilderTest() {
        try { // TOP020+1.p hausdorff problem
            val path = TptpFileFetcher.findProblemFile(TptpDomain.TOP, TptpFormulaForm.FOF, 20, 1)
            TptpFofParser.parseFile(path).let {
                Assert.assertEquals(9, it.inputs.size)
                Assert.assertTrue(it.inputs.all { it is FolAnnotatedFormula })
                Assert.assertTrue(it.inputs.any { it.formulaRole == FormulaRoles.conjecture })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

}
