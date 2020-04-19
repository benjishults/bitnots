package com.benjishults.bitnots.tptp.parser

import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFof
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class TptpProblemFileDescriptorTest {

    @Test
    fun testTptpProblemFileDescriptor() {
        assertTrue(
                TptpProblemFileDescriptor(TptpDomain.AGT, TptpFof, 0, 1) ==
                        TptpProblemFileDescriptor.parseTptpPath(Paths.get("AGT000+1.p")))
        assertTrue(
                TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 10, 2) ==
                        TptpProblemFileDescriptor.parseTptpPath(Paths.get("SYN010+2.p")))
        assertTrue(
                TptpProblemFileDescriptor(TptpDomain.AGT, TptpFof, 0, 1, 0) ==
                        TptpProblemFileDescriptor.parseTptpPath(Paths.get("AGT000+1.000.p")))
    }

}
