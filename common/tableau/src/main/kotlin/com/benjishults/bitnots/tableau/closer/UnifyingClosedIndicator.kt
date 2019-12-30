package com.benjishults.bitnots.tableau.closer

import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.model.unifier.Substitution
import java.util.Stack

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
            // TODO see if this is ever called from a place where this pop is unavoidable.
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

    protected override open fun isCompatible(closer: BranchCloser) =
            if (closer is UnifyingBranchCloser) {
                (closer.sub + substitution).takeIf {
                    it !== NotUnifiable
                }?.let {
                    true to it
                } ?: false to null
            } else
                false to null

    protected override open fun indicatorFactory(branchClosers: List<BranchCloser>, needToClose: Stack<TableauNode>, vararg others: Any): InProgressTableauClosedIndicator =
            others.takeIf { it.isNotEmpty() }?.let { other ->
                UnifyingClosedIndicator(branchClosers, needToClose, other[0] as Substitution)
            } ?: UnifyingClosedIndicator(branchClosers, needToClose, substitution)

}
