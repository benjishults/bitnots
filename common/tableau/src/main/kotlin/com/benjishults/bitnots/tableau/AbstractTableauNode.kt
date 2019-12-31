package com.benjishults.bitnots.tableau

import com.benjishults.bitnots.tableau.closer.BranchCloser
import com.benjishults.bitnots.tableau.strategy.InitializationStrategy
import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.inference.SimpleSignedFormula
import com.benjishults.bitnots.util.TreeNodeImpl

abstract class AbstractTableauNode(
        override val newFormulas: MutableList<SignedFormula<Formula<*>>>,
        parent: TableauNode?,
        val init: InitializationStrategy
) : TreeNodeImpl(parent), TableauNode {

    // starts as proper ancestors and new ones are added after processing
    // TODO see if I can get rid of this or improve it with shared structure
    override val simpleFormulasAbove = mutableListOf<SignedFormula<Formula<*>>>().also { list ->
        parent?.let { parent ->
            list.addAll(parent.newFormulas.filter { it is SimpleSignedFormula<*> })
            list.addAll(parent.simpleFormulasAbove)
        }
    }

    override val branchClosers by lazy {
        mutableListOf<BranchCloser>()
    }

    init {
        init.init(this)
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

}
