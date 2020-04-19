package com.benjishults.bitnots.regression

import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFof
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import org.junit.jupiter.api.Test

class ProblemSetTest {

    @Test
   fun testSynFof() {
        listOf(TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 41, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 73, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 79, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 359, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 360, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 363, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 369, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 381, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 387, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 388, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 394, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 395, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 404, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 405, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 410, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 416, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 721, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 727, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 915, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 928, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 945, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 952, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 953, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 955, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 956, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 958, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 962, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 964, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 972, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 973, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 974, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 975, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFof, 986, 1, 0)
        )
    }

}
