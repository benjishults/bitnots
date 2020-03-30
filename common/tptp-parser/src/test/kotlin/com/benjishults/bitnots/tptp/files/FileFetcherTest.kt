package com.benjishults.bitnots.tptp.files

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("localTptp")
class FileFetcherTest {

    @Test
    fun findProblemDescriptors() {

        val descriptors = TptpFileFetcher.findAllDescriptors(TptpDomain.SYN, TptpFormulaForm.FOF)
        Assertions.assertEquals(9, descriptors.size)
        Assertions.assertTrue(TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 7, 1, 14) in descriptors)
    }

    @Test
    private fun findAxiomFiles() {
        var path = TptpFileFetcher.findAxiomsFile(TptpDomain.SET, TptpFormulaForm.FOF, 7, 118)
        println(path.toUri().toString())
        Assertions.assertTrue(path.toFile().exists())
         path = TptpFileFetcher.findAxiomsFile(TptpDomain.ANA, TptpFormulaForm.CNF)
        println(path.fileName)
        Assertions.assertTrue(path.toFile().exists())
    }

}
