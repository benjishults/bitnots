package com.benjishults.bitnots.tptp.files

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("localTptp")
class FileFetcherTest {

    @Test
    fun testFileFetcher() {
        var path = TptpFileFetcher.findAxiomsFile(TptpDomain.ANA, TptpFormulaForm.CNF)
        println(path.fileName)
        Assertions.assertTrue(path.toFile().exists())
        path = TptpFileFetcher.findAxiomsFile(TptpDomain.SET, TptpFormulaForm.FOF, 7, 118)
        println(path.toUri().toString())
        Assertions.assertTrue(path.toFile().exists())
    }

}
