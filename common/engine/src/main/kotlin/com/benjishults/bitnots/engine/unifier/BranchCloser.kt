package com.benjishults.bitnots.engine.unifier

import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.engine.proof.strategy.BranchCloser
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term

class UnifyingBranchCloser(val lowest: TableauNode, val substitution: Map<FreeVariable, Term<*>>): BranchCloser {

}