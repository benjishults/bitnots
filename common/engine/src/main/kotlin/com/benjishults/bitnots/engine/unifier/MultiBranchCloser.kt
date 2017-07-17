package com.benjishults.bitnots.engine.unifier

import java.util.Stack
import com.benjishults.bitnots.engine.proof.TableauNode

class MultiBranchCloser {
	
	/**
	 * The nodes in this stack are not known to be closed by the receiver.
	 */
	val needToClose = Stack<TableauNode>()
	
	
}