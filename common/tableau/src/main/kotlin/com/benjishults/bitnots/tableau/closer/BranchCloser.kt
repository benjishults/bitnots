package com.benjishults.bitnots.tableau.closer

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.util.ComposableBranchClosureAttempt
import com.benjishults.bitnots.util.Match

class BranchCloser(
    val pos: SignedFormula<*>? = null,
    val neg: SignedFormula<*>? = null,
    val sub: ComposableBranchClosureAttempt<*, *> = EmptySub
) {

    // override val branchCloser: Boolean
    //     get() = sub.branchCloser || neg is ClosingFormula || pos is ClosingFormula
    // override val onlyPotentionBranchCloser: Boolean
    //     get() = sub.onlyPotentionBranchCloser

    fun getMatchOrNull(): Match? {
        return (sub.takeIf { it is Match }
            ?: pos.takeIf { it is ClosingFormula }
            ?: neg.takeIf { it is ClosingFormula }) as Match?
    }

}
