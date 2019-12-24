package com.benjishults.bitnots.engine.proof

import com.benjishults.bitnots.engine.proof.closer.BranchCloser
import com.benjishults.bitnots.engine.proof.strategy.InitializingStrategy
import com.benjishults.bitnots.engine.proof.strategy.PropositionalInitializationStrategy
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.util.TreeNode
import com.benjishults.bitnots.util.TreeNodeImpl
import com.benjishults.bitnots.theory.formula.SignedFormula
import com.benjishults.bitnots.theory.formula.SimpleSignedFormula

interface TableauNode : TreeNode {

    val newFormulas: MutableList<SignedFormula<Formula<*>>>
    fun isClosed(): Boolean = branchClosers.isNotEmpty()
    val branchClosers: MutableList<BranchCloser>
    val simpleFormulasAbove: MutableList<SignedFormula<Formula<*>>>

}

abstract class AbstractTableauNode(
        override val newFormulas: MutableList<SignedFormula<Formula<*>>>,
        parent: TableauNode?,
        val init: InitializingStrategy
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

class PropositionalTableauNode(
        newFormulas: MutableList<SignedFormula<Formula<*>>> = mutableListOf(),
        parent: PropositionalTableauNode? = null,
        init: InitializingStrategy = PropositionalInitializationStrategy()
) : AbstractTableauNode(newFormulas, parent, init)

class FolTableauNode(
        newFormulas: MutableList<SignedFormula<Formula<*>>> = mutableListOf(),
        parent: FolTableauNode? = null,
        init: InitializingStrategy = PropositionalInitializationStrategy()
) : AbstractTableauNode(newFormulas, parent, init)
