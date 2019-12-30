package com.benjishults.bitnots.tableau.closer

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
