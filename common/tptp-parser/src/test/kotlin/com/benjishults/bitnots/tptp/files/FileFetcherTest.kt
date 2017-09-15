package com.benjishults.bitnots.tptp.files

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class FileFetcherTest {

    val fetcher = TptpFileFetcher()

    @Test
    @Ignore // ignoring because the TPTP library may not be installed everywhere.
    fun testFileFetcher() {
        var path = fetcher.findAxiomsFile(TptpDomain.ANA, TptpFormulaForm.CNF)
        println(path.fileName)
        Assert.assertTrue(path.toFile().exists())
        path = fetcher.findAxiomsFile(TptpDomain.SET, TptpFormulaForm.FOF, 7, 118)
        println(path.toUri().toString())
        Assert.assertTrue(path.toFile().exists())
    }

}