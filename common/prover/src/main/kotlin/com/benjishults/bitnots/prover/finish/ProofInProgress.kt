package com.benjishults.bitnots.prover.finish

import com.benjishults.bitnots.util.StepCounter

interface ProofInProgress : StepCounter {
    var indicator: ProofProgressIndicator
}
