package com.benjishults.bitnots.tableau.closer

import com.benjishults.bitnots.tableau.TableauNode
import java.util.Stack

open class BooleanClosedIndicator protected constructor(
        override val branchClosers: List<BranchCloser>
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

    protected lateinit var needToClose: Stack<TableauNode>

    override fun isCloser() =
            needToClose.isEmpty()

    override fun nextNode(): TableauNode =
            needToClose.peek()

    protected open fun indicatorFactory(branchClosers: List<BranchCloser>, needToClose: Stack<TableauNode>, vararg others: Any): InProgressTableauClosedIndicator =
            BooleanClosedIndicator(branchClosers, needToClose)

    protected open fun isCompatible(closer: BranchCloser): Pair<Boolean, Any?> =
            true to null

    override fun createExtension(closer: BranchCloser): InProgressTableauClosedIndicator =
            isCompatible(closer).let { (isCompat, other) ->
                if (isCompat)
                    if (other !== null)
                        indicatorFactory(branchClosers + closer, needToClose, other)
                    else
                        indicatorFactory(branchClosers + closer, needToClose)
                else
                    NotCompatible
            }

    override fun progress(): InProgressTableauClosedIndicator =
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
