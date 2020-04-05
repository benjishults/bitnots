package com.benjishults.bitnots.regression

import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import org.junit.jupiter.api.Test

class ProblemSetTest {

    @Test
   fun testSynFof() {
        listOf(TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 41, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 73, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 79, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 359, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 360, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 363, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 369, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 381, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 387, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 388, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 394, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 395, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 404, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 405, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 410, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 416, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 721, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 727, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 915, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 928, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 945, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 952, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 953, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 955, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 956, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 958, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 962, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 964, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 972, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 973, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 974, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 975, 1, -1),
               TptpProblemFileDescriptor(TptpDomain.SYN, TptpFormulaForm.FOF, 986, 1, 0)
        )
    }

}
