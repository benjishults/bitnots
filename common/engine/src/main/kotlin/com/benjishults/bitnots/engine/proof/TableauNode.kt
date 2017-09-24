package com.benjishults.bitnots.engine.proof

import com.benjishults.bitnots.engine.proof.strategy.BooleanClosedIndicator
import com.benjishults.bitnots.engine.proof.strategy.ClosedIndicator
import com.benjishults.bitnots.engine.proof.strategy.ClosingStrategy
import com.benjishults.bitnots.engine.proof.strategy.FolUnificationClosingStrategy
import com.benjishults.bitnots.engine.proof.strategy.InitializingStrategy
import com.benjishults.bitnots.engine.proof.strategy.PropositionalClosingStrategy
import com.benjishults.bitnots.engine.proof.strategy.PropositionalInitializationStrategy
import com.benjishults.bitnots.engine.unifier.MultiBranchCloser
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.util.TreeNode
import com.benjishults.bitnots.model.util.TreeNodeImpl

interface TableauNode<C : ClosedIndicator> : TreeNode {

    val newFormulas: MutableList<SignedFormula<Formula<*>>>
    fun isClosed(): Boolean

}

abstract class AbstractTableauNode<C : ClosedIndicator>(
        override val newFormulas: MutableList<SignedFormula<Formula<*>>>,
        parent: AbstractTableauNode<C>?,
        val closer: ClosingStrategy<AbstractTableauNode<C>, C>,
        val init: InitializingStrategy<AbstractTableauNode<C>>
) : TreeNodeImpl(parent), TableauNode<C> {

    // starts as proper ancestors and new ones are added after processing
    // TODO see if I can get rid of this or improve it with shared structure
    val allFormulas = mutableListOf<SignedFormula<Formula<*>>>().also { list ->
        parent?.toAncestors<AbstractTableauNode<C>> { node ->
            list.addAll(node.newFormulas.filter {
                it is SimpleSignedFormula<*>
            })
        }
    }

    private var closed = false

    override fun isClosed() =
            if (closed || closer.checkClosed(this).isCloser() ||
                    (children.isNotEmpty() && children.all {
                        (it as PropositionalTableauNode).isClosed()
                    })) {
                closed = true
                true
            } else
                false

    val initialClosers by lazy {
        mutableListOf<Substitution>()
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
        closer: ClosingStrategy<AbstractTableauNode<BooleanClosedIndicator>, BooleanClosedIndicator> = PropositionalClosingStrategy(),
        init: InitializingStrategy<AbstractTableauNode<*>> = PropositionalInitializationStrategy()
) : AbstractTableauNode<BooleanClosedIndicator>(newFormulas, parent, closer, init) {

//    init {
//        allFormulas.addAll(newFormulas)
//    }

}

class FolTableauNode(
        newFormulas: MutableList<SignedFormula<Formula<*>>> = mutableListOf(),
        parent: FolTableauNode? = null,
        closer: ClosingStrategy<AbstractTableauNode<MultiBranchCloser>, MultiBranchCloser> = FolUnificationClosingStrategy(),
        init: InitializingStrategy<AbstractTableauNode<MultiBranchCloser>> = PropositionalInitializationStrategy()
) : AbstractTableauNode<MultiBranchCloser>(newFormulas, parent, closer, init) {

//    override fun isClosed(): FolUnificationClosedIndicator {
//        TODO()
//    }


    fun generateClosers(node: AbstractTableauNode<*>) {
        with(node) {
            allFormulas.filter {
                it.sign
            }.forEach { above ->
                newFormulas.filter {
                    !it.sign
                }.forEach {
                    Formula.unify(above.formula, it.formula, EmptySub).let {
                        initialClosers.add(it)
                    }
                }
            }
        }
    }

    /**
     * Finds all branch closers at every descendant of the receiver and stores them at the highest node at which all formulas involved occur.
     */
    fun findAllClosers() {

    }

    fun extendMbc() {

    }

}
