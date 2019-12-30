package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.tableau.closer.BranchCloser
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.util.TreeNode

interface TableauNode : TreeNode {

    val newFormulas: MutableList<SignedFormula<Formula<*>>>
    fun isClosed(): Boolean = branchClosers.isNotEmpty()
    val branchClosers: MutableList<BranchCloser>
    val simpleFormulasAbove: MutableList<SignedFormula<Formula<*>>>

}
