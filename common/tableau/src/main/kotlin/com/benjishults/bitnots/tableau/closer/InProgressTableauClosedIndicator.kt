package com.benjishults.bitnots.tableau.closer

import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.util.collection.clone
import com.benjishults.bitnots.util.collection.peek
import com.benjishults.bitnots.util.collection.pop
import com.benjishults.bitnots.util.collection.push

sealed class TableauProofProgressIndicator : ProofProgressIndicator

/**
 * Indicates that an attempt was made to extend an InProgressTableauClosedIndicator with a BranchCloser with which it was not compatible.
 */
object ExtensionFailed : TableauProofProgressIndicator() {
    /**
     * @return false
     */
    override fun isDone() = false

}

/**
 * Indicates that the ProofInProgress is doomed and cannot be completed successfully.
 */
object Fail : TableauProofProgressIndicator() {
    /**
     * @return false
     */
    override fun isDone() = false

}

/**
 * We don't care or don't have interesting information about how it was done.
 */
object Done : TableauProofProgressIndicator() {
    /**
     * @return true
     */
    override fun isDone() = true

}

/**
 * A tableau is a proof if its branches all have a compatible closer.
 */
sealed class InProgressTableauClosedIndicator : TableauProofProgressIndicator() {

    /**
     * A compatible list of branch closers.
     */
    abstract val branchClosers: List<BranchCloser>

    /**
     * @return NotCompatible if [closer] is not compatible with the receiver.
     */
    abstract fun createExtension(closer: BranchCloser): TableauProofProgressIndicator

    /**
     *
     * @throws EmptyStackException if all branches are covered
     * @throws IllegalStateException if this is NotCompatible
     * @return a node in the tableau that is not covered by [branchClosers]
     */
    abstract fun nextNode(): TableauNode<*>

    /**
     * @return the result of an attempt to expand the receiver to cover at least one more branch.  Returns NotCompatible is no progress can be made.
     */
    abstract fun progress(): TableauProofProgressIndicator

    abstract fun isCompatible(closer: BranchCloser): Substitution

}

open class BooleanClosedIndicator protected constructor(
        override val branchClosers: List<BranchCloser>
) : InProgressTableauClosedIndicator() {

    protected constructor(
            branchClosers: List<BranchCloser>,
            nodes: MutableList<TableauNode<*>>
    ) : this(branchClosers) {
        needToClose = (nodes.clone()).also {
            // TODO make sure this is helping somewhere.  Because I know there are cases where I push something on
            //      just because I know it's going to be popped.
            it.pop()
        }
    }

    constructor(
            node: TableauNode<*>,
            branchClosers: List<BranchCloser> = emptyList()
    ) : this(branchClosers) {
        needToClose = mutableListOf(node)
    }

    protected lateinit var needToClose: MutableList<TableauNode<*>>

    override fun isDone() =
            needToClose.isEmpty()

    override fun nextNode(): TableauNode<*> =
            needToClose.peek()

    protected open fun indicatorFactory(
            branchClosers: List<BranchCloser>,
            needToClose: MutableList<TableauNode<*>>,
            // TODO seriously, we can do better than this.
            substitution: Substitution = EmptySub
    ): TableauProofProgressIndicator = BooleanClosedIndicator(branchClosers, needToClose)

    override fun isCompatible(closer: BranchCloser): Substitution = EmptySub

    override fun createExtension(closer: BranchCloser): TableauProofProgressIndicator =
            indicatorFactory(branchClosers + closer, needToClose, isCompatible(closer))
            // isCompatible(closer).takeUnless {
            //     }
            //     substitution ->
            //             indicatorFactory(branchClosers + closer, needToClose, substitution)}
            //         ExtensionFailed
            // }

    override fun progress(): TableauProofProgressIndicator =
            nextNode().let { nextNode ->

                nextNode.children.takeIf {
                    it.isNotEmpty()
                }?.reversed()?.let { reversedChildrenOfNextNode ->
                    (needToClose.clone()).let { cloneOfNeedToClose ->
                        cloneOfNeedToClose.pop() // take nextNode off the cloned list
                        // push its children on in reverse
                        // TODO consider if there's a way to do this without reversing
                        reversedChildrenOfNextNode.forEach { node ->
                            cloneOfNeedToClose.push(node)
                        }
                        // pushing this back on so that it will be popped by the constructor
                        cloneOfNeedToClose.push(nextNode)
                        indicatorFactory(branchClosers, cloneOfNeedToClose)
                    }
                } ?: ExtensionFailed
            }

}
