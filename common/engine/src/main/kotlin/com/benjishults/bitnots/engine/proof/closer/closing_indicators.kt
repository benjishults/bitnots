package com.benjishults.bitnots.engine.proof.closer

import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Substitution
import java.util.Stack

interface InProgressTableauClosedIndicator {

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

    fun isCloser(): Boolean

//    fun isCompatible
}

/**
 * It's appearance indicates that an attempt was made to extend an InProgressTableauClosedIndicator with a BranchCloser with which it was not compatible.
 */
object NotCompatible : InProgressTableauClosedIndicator {
    override fun progress() = this

    override fun nextNode() = throw IllegalStateException()

    override val branchClosers: List<BranchCloser> = emptyList()

    override fun createExtension(closer: BranchCloser) = this

    override fun isCloser() = false
}
