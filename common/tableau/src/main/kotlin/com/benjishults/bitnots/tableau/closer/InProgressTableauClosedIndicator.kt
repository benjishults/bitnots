package com.benjishults.bitnots.tableau.closer

import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.tableau.TableauNode

interface InProgressTableauClosedIndicator: ProofProgressIndicator {

    /**
     * returns NotCompatible if the BranchCloser is not compatible with the receiver.
     */
    fun createExtension(closer: BranchCloser): InProgressTableauClosedIndicator

    val branchClosers: List<BranchCloser>

    /**
     * Throws an exception if this is a closer or NotCompatible
     */
    fun nextNode(): TableauNode

    fun progress(): InProgressTableauClosedIndicator

    //    fun isCompatible
}
