package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.theory.formula.FormulaRole
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.nio.file.FileSystems

@Tag("localTptp")
class ParserTest {

    private val path = FileSystems.getDefault().getPath("src/test/resources/PLA001-1.p")

    @Test
    fun cnfParserTest() {
        try {
            TptpCnfParser.parseFile(path).let {
                Assertions.assertEquals(16, it.size)
                Assertions.assertTrue((it.last()).formulaRole === FormulaRole.negated_conjecture)
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
                Assertions.assertEquals(53, it.size)
                Assertions.assertTrue((it.last()).formulaRole === FormulaRole.conjecture)
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
                Assertions.assertEquals(2, it.size)
                Assertions.assertTrue((it.last()).formulaRole === FormulaRole.axiom, "Was ${it.last()}")
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
                Assertions.assertEquals(1, it.size)
                Assertions.assertTrue((it.last()).formulaRole === FormulaRole.conjecture, "Was ${it.last()}")
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
                Assertions.assertEquals(1, it.size)
                Assertions.assertTrue((it.last()).formulaRole === FormulaRole.conjecture, "Was ${it.last()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    @Disabled
    fun fofParserTest5() {
        try { // SYN000+1.p with the two offending sections commented out
            val path = TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 0, 1)
            TptpFofParser.parseFile(path).let {
                Assertions.assertEquals(11, it.size)
                Assertions.assertTrue((it[7]).formulaRole === FormulaRole.conjecture, "Was ${it[7]}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    @Test
    @Disabled
    fun fofParserTest6() {
        try { // SYN000+2.p with the two offending sections commented out
            val path = TptpFileFetcher.findProblemFile(TptpDomain.SYN, TptpFormulaForm.FOF, 0, 2)
            TptpFofParser.parseFile(path).let {
                Assertions.assertEquals(16, it.size)
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
                Assertions.assertEquals(9, it.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

}
