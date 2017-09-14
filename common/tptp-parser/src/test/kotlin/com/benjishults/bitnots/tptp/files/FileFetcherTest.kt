package com.benjishults.bitnots.tptp.files

import org.junit.Test
import org.junit.Assert

class FileFetcherTest {

    val fetcher = TptpFileFetcher()

    @Test
    fun testFileFetcher() {
        var path = fetcher.findAxiomsFile(TptpDomain.ANA, TptpFormulaForm.CNF)
        println(path.fileName)
        Assert.assertTrue(path.toFile().exists())
        path = fetcher.findAxiomsFile(TptpDomain.SET, TptpFormulaForm.FOF, 7, 118)
        println(path.toUri().toString())
        Assert.assertTrue(path.toFile().exists())
    }

}