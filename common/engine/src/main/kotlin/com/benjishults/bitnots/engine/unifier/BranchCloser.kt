package com.benjishults.bitnots.engine.unifier

import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term

class BranchCloser(val lowest: TableauNode<*>, val substitution: Map<FreeVariable, Term<*>>) {

}