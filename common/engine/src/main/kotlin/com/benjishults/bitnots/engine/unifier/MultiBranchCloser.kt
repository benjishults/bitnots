package com.benjishults.bitnots.engine.unifier

import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.engine.proof.strategy.BranchCloser
import com.benjishults.bitnots.engine.proof.strategy.InProgressTableauClosedIndicator
import java.util.Stack

open class MultiBranchCloser : InProgressTableauClosedIndicator {

    override fun createExtension(closer: BranchCloser): InProgressTableauClosedIndicator {
        return MultiBranchCloser()
    }


    /**
     * The nodes in this stack are not known to be closed by the receiver.
     */
    override val needToClose = Stack<TableauNode>()

}
