package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.prover.finish.ProofInProgress

interface Tableau<TN : TableauNode<TN>> : ProofInProgress {

    val root: TN

}
