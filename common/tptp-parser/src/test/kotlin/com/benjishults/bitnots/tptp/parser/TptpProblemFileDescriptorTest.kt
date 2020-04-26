package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.tptp.files.TptpCnf
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFof
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

class TptpProblemFileDescriptorTest {

    @Test
    fun testTptpProblemFileDescriptor() {
        assertTrue(
            TptpProblemFileDescriptor(TptpDomain.AGT, TptpFof, 0, 1) ==
                    TptpProblemFileDescriptor.fromFileName("AGT000+1.p")
        )
        assertTrue(
            TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 10, 2) ==
                    TptpProblemFileDescriptor.fromFileName("SYN010+2.p")
        )
        assertTrue(
            TptpProblemFileDescriptor(TptpDomain.AGT, TptpFof, 0, 1, 0) ==
                    TptpProblemFileDescriptor.fromFileName("AGT000+1.000.p")
        )
    }

    @Test
    @Tag("localTptp")
    fun testTptpProblemFileDescriptorFindAllMatches() {
        runBlocking {
            assertTrue(TptpFileFetcher.findAllDescriptors(TptpDomain.TOP, TptpFof).isNotEmpty())
            assertTrue(TptpFileFetcher.findAllDescriptors(TptpDomain.TOP, TptpCnf).isNotEmpty())
        }
    }

}
