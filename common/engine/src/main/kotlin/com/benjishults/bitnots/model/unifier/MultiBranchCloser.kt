package com.benjishults.bitnots.model.unifier

import java.util.Stack
import com.benjishults.bitnots.model.proof.TableauNode

class MultiBranchCloser {
	
	/**
	 * The nodes in this stack are not known to be closed by the receiver.
	 */
	val needToClose = Stack<TableauNode>()
	
	
}