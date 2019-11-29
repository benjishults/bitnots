package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.theory.formula.CnfAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FolAnnotatedFormula
import com.benjishults.bitnots.theory.formula.FormulaRoles
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import org.junit.Assert
import org.junit.Test
import java.nio.file.FileSystems

class ParserTest {

    private val path = FileSystems.getDefault().getPath("src/test/resources/PLA001-1.p")

    @Test
    fun cnfParserTest() {
        try {
            TptpCnfParser.parseFile(path).let {
                Assert.assertEquals(16, it.inputs.size)
                Assert.assertTrue(it.inputs.all { it is CnfAnnotatedFormula })
                Assert.assertTrue((it.inputs.last() as CnfAnnotatedFormula).formulaRole === FormulaRoles.negated_conjecture)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    fun fofParserTest1() {
        try {
            val path = TptpFileFetcher.findProblemFile(TptpDomain.PLA, TptpFormulaForm.FOF, 24, 1)
            TptpFofParser.parseFile(path).let {
                Assert.assertEquals(53, it.inputs.size)
                Assert.assertTrue(it.inputs.all { it is FolAnnotatedFormula })
                Assert.assertTrue((it.inputs.last() as FolAnnotatedFormula).formulaRole === FormulaRoles.conjecture)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    fun fofParserTest2() {
        try {
            val path = TptpFileFetcher.findProblemFile(TptpDomain.PLA, TptpFormulaForm.FOF, 34, 1)
            TptpFofParser.parseFile(path).let {
                Assert.assertEquals(2, it.inputs.size)
                Assert.assertTrue(it.inputs.all { it is FolAnnotatedFormula })
                Assert.assertTrue("Was ${it.inputs.last()}", (it.inputs.last() as FolAnnotatedFormula).formulaRole === FormulaRoles.axiom)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    fun fofParserTest3() {
        try {
            val path = TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 981, 1)
            TptpFofParser.parseFile(path).let {
                Assert.assertEquals(1, it.inputs.size)
                Assert.assertTrue(it.inputs.all { it is FolAnnotatedFormula })
                Assert.assertTrue("Was ${it.inputs.last()}", (it.inputs.last() as FolAnnotatedFormula).formulaRole === FormulaRoles.conjecture)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    fun fofParserTest4() {
        try { // SYN078+1.p
            val path = TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 78, 1)
            TptpFofParser.parseFile(path).let {
                Assert.assertEquals(1, it.inputs.size)
                Assert.assertTrue(it.inputs.all { it is FolAnnotatedFormula })
                Assert.assertTrue("Was ${it.inputs.last()}", (it.inputs.last() as FolAnnotatedFormula).formulaRole === FormulaRoles.conjecture)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    fun fofParserTest5() {
        try { // SYN000+1.p with the two offending sections commented out
            val path = TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 0, 1)
            TptpFofParser.parseFile(path).let {
                Assert.assertEquals(11, it.inputs.size)
                Assert.assertTrue(it.inputs.all { it is FolAnnotatedFormula })
                Assert.assertTrue("Was ${it.inputs[7]}", (it.inputs[7] as FolAnnotatedFormula).formulaRole === FormulaRoles.conjecture)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    fun fofParserTest6() {
        try { // SYN000+2.p with the two offending sections commented out
            val path = TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 0, 2)
            TptpFofParser.parseFile(path).let {
                Assert.assertEquals(16, it.inputs.size)
                Assert.assertTrue(it.inputs.all { it is FolAnnotatedFormula })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

}
