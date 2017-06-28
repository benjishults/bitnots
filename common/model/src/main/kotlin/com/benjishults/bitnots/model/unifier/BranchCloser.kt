package com.benjishults.bitnots.model.unifier

import com.benjishults.bitnots.model.proof.TableauNode
import com.benjishults.bitnots.model.terms.FreeVariable
import com.benjishults.bitnots.model.terms.Term

class BranchCloser(val lowest: TableauNode, val substitution: Map<FreeVariable, Term>) {

}