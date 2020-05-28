package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.prover.finish.TimedProofInProgress
import com.benjishults.bitnots.tableau.closer.RanOutOfRunwayTableauProgressIndicator
import com.benjishults.bitnots.util.Counter
import com.benjishults.bitnots.util.Timed

abstract class Tableau<TN : TableauNode<TN>> : TimedProofInProgress, Counter by Counter(), Timed by Timed() {

    abstract val root: TN

    @Volatile
    override var indicator: ProofProgressIndicator = RanOutOfRunwayTableauProgressIndicator

}
