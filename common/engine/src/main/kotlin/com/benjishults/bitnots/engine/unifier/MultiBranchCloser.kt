package com.benjishults.bitnots.engine.unifier

import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.engine.proof.strategy.ClosedIndicator
import java.util.Stack

class MultiBranchCloser : ClosedIndicator {
    override fun isCloser(): Boolean =
            needToClose.isEmpty()

    /**
     * The nodes in this stack are not known to be closed by the receiver.
     */
    val needToClose = Stack<TableauNode<*>>()

}
