package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.tableau.closer.BranchCloser
import com.benjishults.bitnots.util.tree.TreeNodeImpl

open class TableauNode<TN: TableauNode<TN>>(
        val newFormulas: MutableList<SignedFormula<Formula<*>>>,
        parent: TN? = null
) : TreeNodeImpl<TN>(parent) {

    // starts as proper ancestors and new ones are added after processing
    // TODO see if I can get rid of this or improve it with shared structure
    val simpleFormulasAbove: MutableList<SignedFormula<Formula<*>>> = mutableListOf<SignedFormula<Formula<*>>>().also { list ->
        parent?.let { nonNullParent ->
            list.addAll(nonNullParent.newFormulas.filterIsInstance<SimpleSignedFormula<*>>())
            list.addAll(nonNullParent.simpleFormulasAbove)
        }
    }

    val branchClosers by lazy {
        mutableListOf<BranchCloser>()
    }

    override fun toString(): String {
        return StringBuilder().also {
            if (newFormulas.any { it.sign })
                it.append("Suppose: " + newFormulas.filter {
                    it.sign
                }.map {
                    it.formula
                }.joinToString("\n") + "\n")
            if (newFormulas.any {
                    !it.sign
                })
                it.append("Show: " + newFormulas.filter {
                    !it.sign
                }.map {
                    it.formula
                }.joinToString("\n") + "\n")
            if (children.isNotEmpty())
                children.joinToString("\n")
        }.toString();
    }

    fun isClosed(): Boolean = branchClosers.isNotEmpty()

}
