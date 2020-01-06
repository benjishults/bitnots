package com.benjishults.bitnots.tableau.closer

import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotCompatible
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.util.collection.clone
import com.benjishults.bitnots.util.collection.pop

open class UnifyingClosedIndicator private constructor(
        branchClosers: List<BranchCloser>,
        val substitution: Substitution
) : BooleanClosedIndicator(branchClosers) {

    private constructor(
            branchClosers: List<BranchCloser>,
            nodesToClose: MutableList<TableauNode<*>>,
            substitution: Substitution
    ) : this(branchClosers, substitution) {
        needToClose = (nodesToClose.clone()).apply {
            // TODO see if this is ever called from a place where this pop is unavoidable.
            pop()
        }
    }

    constructor(
            node: TableauNode<*>,
            branchClosers: List<BranchCloser> = emptyList(),
            substitution: Substitution = EmptySub
    ) : this(branchClosers, substitution) {
        needToClose = mutableListOf(node)
    }

    override fun isCompatible(closer: BranchCloser) = // TODO why not just return the Substitution and let is speak for itself?
            if (closer is UnifyingBranchCloser) {
                (closer.sub + substitution).takeIf {
                    it !== NotCompatible
                }?.let {
                    true to it
                } ?: false to null
            } else
                false to null

    override fun indicatorFactory(branchClosers: List<BranchCloser>, needToClose: MutableList<TableauNode<*>>,
                                  vararg others: Any): InProgressTableauClosedIndicator =
            others.takeIf {
                it.isNotEmpty()
            }?.let { other ->
                UnifyingClosedIndicator(branchClosers, needToClose, other[0] as Substitution)
            } ?: UnifyingClosedIndicator(branchClosers, needToClose, substitution)

}
