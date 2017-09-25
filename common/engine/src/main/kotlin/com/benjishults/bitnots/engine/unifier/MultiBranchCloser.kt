package com.benjishults.bitnots.engine.unifier

import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.engine.proof.strategy.BranchCloser
import com.benjishults.bitnots.engine.proof.strategy.InProgressTableauClosedIndicator
import java.util.Stack

open class MultiBranchCloser : InProgressTableauClosedIndicator {
    
    override val branchClosers: List<BranchCloser>
        get() = TODO()

    /**
     * The nodes in this stack are not known to be closed by the receiver.
     */
    private val needToClose = Stack<TableauNode>()

    override fun nextNode(): TableauNode = needToClose.peek()

    override fun isCloser(): Boolean {
        TODO()
    }

    override fun createExtension(closer: BranchCloser): InProgressTableauClosedIndicator {
        return MultiBranchCloser()
    }

    override fun progress(): InProgressTableauClosedIndicator {
        TODO()
    }

}
