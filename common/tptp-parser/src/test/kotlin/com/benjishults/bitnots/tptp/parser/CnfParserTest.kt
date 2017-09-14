package com.benjishults.bitnots.tptp.parser

import org.junit.Test
import java.nio.file.FileSystems
import org.junit.Assert

class CnfParserTest {

    private val path = FileSystems.getDefault().getPath("src/test/resources/PLA001-1.p")

    @Test
    fun parserTest() {
        path.toFile().bufferedReader().use {
            try {
                TptpParser.parseFile(path).let {
                    Assert.assertEquals(16, it.inputs.size)
                    Assert.assertTrue(it.inputs.all { it is CnfAnnotatedFormula })
                    Assert.assertTrue((it.inputs.last() as CnfAnnotatedFormula).formulaRole == "negated_conjecture")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }
}
