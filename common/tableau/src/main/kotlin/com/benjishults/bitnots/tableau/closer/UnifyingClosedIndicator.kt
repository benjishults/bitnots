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

    // TODO maybe get rid of this
    override fun isCompatible(closer: BranchCloser) =
            closer.sub + substitution

    override fun indicatorFactory(
            branchClosers: List<BranchCloser>,
            needToClose: MutableList<TableauNode<*>>,
            substitution: Substitution
    ) =
            when (substitution) {
                NotCompatible -> ExtensionFailed
                else          -> UnifyingClosedIndicator(branchClosers, needToClose, substitution)
            }


}
