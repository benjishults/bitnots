package com.benjishults.bitnots.engine.proof

import com.benjishults.bitnots.engine.proof.strategy.BooleanClosedIndicator
import com.benjishults.bitnots.engine.proof.strategy.ClosingStrategy
import com.benjishults.bitnots.engine.proof.strategy.InProgressTableauClosedIndicator
import com.benjishults.bitnots.engine.proof.strategy.InitializingStrategy
import com.benjishults.bitnots.engine.proof.strategy.PropositionalInitializationStrategy
import com.benjishults.bitnots.engine.unifier.MultiBranchCloser
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.util.TreeNode
import com.benjishults.bitnots.model.util.TreeNodeImpl
import com.benjishults.bitnots.engine.proof.strategy.BranchCloser

interface TableauNode : TreeNode {

    val newFormulas: MutableList<SignedFormula<Formula<*>>>
    fun isClosed(): Boolean = branchClosers.isNotEmpty()
    val branchClosers: MutableList<BranchCloser>
    val allFormulas: MutableList<SignedFormula<Formula<*>>>
}

abstract class AbstractTableauNode(
        override val newFormulas: MutableList<SignedFormula<Formula<*>>>,
        parent: AbstractTableauNode?,
//        val closer: ClosingStrategy,
        val init: InitializingStrategy
) : TreeNodeImpl(parent), TableauNode {

    // starts as proper ancestors and new ones are added after processing
    // TODO see if I can get rid of this or improve it with shared structure
    override val allFormulas = mutableListOf<SignedFormula<Formula<*>>>().also { list ->
        parent?.toAncestors<AbstractTableauNode> { node ->
            list.addAll(node.newFormulas.filter {
                it is SimpleSignedFormula<*>
            })
        }
    }
    override val branchClosers by lazy { mutableListOf<BranchCloser>() }


//    private var closed = false
//
//    open override fun isClosed() =
//            closed ||
//                    closer.checkClosed(this).isCloser() ||
//                    (children.isNotEmpty() &&
//                            children.all {
//                                (it as TableauNode).isClosed()
//                            })

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
//        closer: ClosingStrategy = PropositionalClosingStrategy(),
        init: InitializingStrategy = PropositionalInitializationStrategy()
) : AbstractTableauNode(newFormulas, parent, init) {
}

//class FolTableauNode(
//        newFormulas: MutableList<SignedFormula<Formula<*>>> = mutableListOf(),
//        parent: FolTableauNode? = null,
//        closer: ClosingStrategy = FolUnificationClosingStrategy(),
//        init: InitializingStrategy<AbstractTableauNode> = PropositionalInitializationStrategy()
//) : AbstractTableauNode(newFormulas, parent, closer, init) {
//
////    override fun isClosed(): FolUnificationClosedIndicator {
////        TODO()
////    }
//
//
//    override fun isClosed() =
//            //            closed ||
//            closer.checkClosed(this).isCloser() ||
//                    (children.isNotEmpty() &&
//                            children.all {
//                                (it as TableauNode).isClosed()
//                            })
//
//    fun generateClosers(node: AbstractTableauNode) {
//        with(node) {
//            allFormulas.filter {
//                it.sign
//            }.forEach { above ->
//                newFormulas.filter {
//                    !it.sign
//                }.forEach {
//                    Formula.unify(above.formula, it.formula, EmptySub).let {
//                        initialClosers.add(it)
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Finds all branch closers at every descendant of the receiver and stores them at the highest node at which all formulas involved occur.
//     */
//    fun findAllClosers() {
//
//    }
//
//    fun extendMbc() {
//
//    }
//
//}
