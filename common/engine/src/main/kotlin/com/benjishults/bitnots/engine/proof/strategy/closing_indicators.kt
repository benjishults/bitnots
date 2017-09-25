package com.benjishults.bitnots.engine.proof.strategy

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

open class BooleanClosedIndicator protected constructor(override val branchClosers: List<BranchCloser>
) : InProgressTableauClosedIndicator {

    protected constructor(
            branchClosers: List<BranchCloser>,
            nodes: Stack<TableauNode>
    ) : this(branchClosers) {
        @Suppress("UNCHECKED_CAST")
        needToClose = (nodes.clone() as Stack<TableauNode>).apply {
            // NB
            pop()
        }
    }

    constructor(
            node: TableauNode,
            branchClosers: List<BranchCloser> = emptyList()
    ) : this(branchClosers) {
        needToClose = Stack<TableauNode>().apply {
            push(node)
        }
    }

    protected open val indicatorFactory: (List<BranchCloser>, Stack<TableauNode>) -> InProgressTableauClosedIndicator =
            { bc, st ->
                BooleanClosedIndicator(bc, st)
            }

    protected lateinit var needToClose: Stack<TableauNode>

    override fun isCloser() =
            needToClose.isEmpty()

    override fun nextNode(): TableauNode =
            needToClose.peek()

    open override fun createExtension(closer: BranchCloser): InProgressTableauClosedIndicator {
        return indicatorFactory(branchClosers + closer, needToClose)
    }

    open override fun progress(): InProgressTableauClosedIndicator =
            @Suppress("UNCHECKED_CAST")
            (nextNode().children as List<TableauNode>).takeIf {
                it.isNotEmpty()
            }?.reversed()?.let {
                (needToClose.clone() as Stack<TableauNode>).apply {
                    val orig = pop()
                    it.forEach {
                        push(it)
                    }
                    push(orig) // pushing this back on so that it will be popped by the constructor
                }.let { newNeeds ->
                    indicatorFactory(branchClosers, newNeeds)
                }
            } ?: NotCompatible

}

open class UnifyingClosedIndicator protected constructor(
        branchClosers: List<BranchCloser>,
        val substitution: Substitution
) : BooleanClosedIndicator(branchClosers) {

    protected constructor(
            branchClosers: List<BranchCloser>,
            nodes: Stack<TableauNode>,
            substitution: Substitution
    ) : this(branchClosers, substitution) {
        @Suppress("UNCHECKED_CAST")
        needToClose = (nodes.clone() as Stack<TableauNode>).apply {
            // NB
            pop()
        }
    }

    constructor(
            node: TableauNode,
            branchClosers: List<BranchCloser> = emptyList(),
            substitution: Substitution = EmptySub
    ) : this(branchClosers, substitution) {
        needToClose = Stack<TableauNode>().apply {
            push(node)
        }
    }

    fun isCompatible(bc: Any?, st: Any?) = NotCompatible

    open override fun createExtension(closer: BranchCloser): InProgressTableauClosedIndicator =
            if (closer is UnifyingBranchCloser) {
                // FIXME
//                closer.sub
                indicatorFactory(branchClosers + closer, needToClose)
            } else
                NotCompatible

    protected override open val indicatorFactory: (List<BranchCloser>, Stack<TableauNode>) -> InProgressTableauClosedIndicator =
            { bc, st ->
                isCompatible(bc, st)
            }

}
