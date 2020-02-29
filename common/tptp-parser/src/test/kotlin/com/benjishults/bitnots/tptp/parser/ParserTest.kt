package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.theory.formula.FormulaRole
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.nio.file.FileSystems

class ParserTest {

    private val path = FileSystems.getDefault().getPath("src/test/resources/PLA001-1.p")

    @Test
    fun cnfParserTest() {
        try {
            TptpCnfParser.parseFile(path).let {
                Assert.assertEquals(16, it.size)
                Assert.assertTrue((it.last()).formulaRole === FormulaRole.negated_conjecture)
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
                Assert.assertEquals(53, it.size)
                Assert.assertTrue((it.last()).formulaRole === FormulaRole.conjecture)
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
                Assert.assertEquals(2, it.size)
                Assert.assertTrue("Was ${it.last()}", (it.last()).formulaRole === FormulaRole.axiom)
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
                Assert.assertEquals(1, it.size)
                Assert.assertTrue("Was ${it.last()}", (it.last()).formulaRole === FormulaRole.conjecture)
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
                Assert.assertEquals(1, it.size)
                Assert.assertTrue("Was ${it.last()}",
                                  (it.last()).formulaRole === FormulaRole.conjecture)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    @Ignore
    fun fofParserTest5() {
        try { // SYN000+1.p with the two offending sections commented out
            val path = TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 0, 1)
            TptpFofParser.parseFile(path).let {
                Assert.assertEquals(11, it.size)
                Assert.assertTrue("Was ${it[7]}", (it[7]).formulaRole === FormulaRole.conjecture)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    @Ignore
    fun fofParserTest6() {
        try { // SYN000+2.p with the two offending sections commented out
            val path = TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 0, 2)
            TptpFofParser.parseFile(path).let {
                Assert.assertEquals(16, it.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    fun fofParserTest7() {
        try { // TOP020+1.p hausdorff problem
            val path = TptpFileFetcher.findProblemFile(TptpDomain.TOP, TptpFormulaForm.FOF, 20, 1)
            TptpFofParser.parseFile(path).let {
                Assert.assertEquals(9, it.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

}
