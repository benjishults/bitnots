package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.tableau.closer.RanOutOfRunwayTableauProgressIndicator
import com.benjishults.bitnots.util.StepCounter
import com.benjishults.bitnots.util.StepCounterImpl

abstract class Tableau<TN : TableauNode<TN>> : ProofInProgress, StepCounter by StepCounterImpl() {

    abstract val root: TN

    @Volatile
    override var indicator: ProofProgressIndicator = RanOutOfRunwayTableauProgressIndicator

}
