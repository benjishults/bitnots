package com.benjishults.bitnots.prover.finish

import com.benjishults.bitnots.util.Counter

interface ProofInProgress : Counter {
    var indicator: ProofProgressIndicator
}
