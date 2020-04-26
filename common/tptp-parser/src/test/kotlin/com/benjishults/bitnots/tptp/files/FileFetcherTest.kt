package com.benjishults.bitnots.tptp.files

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("localTptp")
class FileFetcherTest {

    @Test
    fun findProblemDescriptors() {
        runBlocking {
            val descriptors = TptpFileFetcher.findAllDescriptors(TptpDomain.SYN, TptpFof)
            Assertions.assertEquals(375, descriptors.size)
            Assertions.assertTrue(TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 7, 1, 14) in descriptors)
        }
    }

    @Test
    private fun findAxiomFiles() {
        var path = TptpFileFetcher.findAxiomsFile(TptpDomain.SET, TptpFof, 7, 118)
        println(path.toUri().toString())
        Assertions.assertTrue(path.toFile().exists())
        path = TptpFileFetcher.findAxiomsFile(TptpDomain.ANA, TptpCnf)
        println(path.fileName)
        Assertions.assertTrue(path.toFile().exists())
    }

}
