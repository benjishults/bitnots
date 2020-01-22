package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.prover.finish.ProofInProgress
import com.benjishults.bitnots.tableau.closer.RanOutOfRunwayTableauProgressIndicator
import com.benjishults.bitnots.tableau.closer.TableauProofProgressIndicator

interface Tableau<TN : TableauNode<TN>> : ProofInProgress {

    val root: TN

    /**
     * If the tableau is closed, closer().isDone() should return [true].
     */
    fun closer(): TableauProofProgressIndicator = RanOutOfRunwayTableauProgressIndicator

}
