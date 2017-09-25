package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.AbstractTableauNode
import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.engine.unifier.MultiBranchCloser
import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.inference.rules.BetaFormula
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.inference.rules.DeltaFormula
import com.benjishults.bitnots.inference.rules.GammaFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.fol.VarBindingFormula
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import java.util.Stack
import java.util.TreeSet

interface BranchCloser

data class PropositionalBranchCloser(val pos: SignedFormula<*>? = null, val neg: SignedFormula<*>? = null) : BranchCloser

interface TableauClosingStrategy {
    fun populateBranchClosers(tableau: Tableau)
}

object PropositionalClosingStrategy : TableauClosingStrategy {
    override fun populateBranchClosers(tableau: Tableau) {
        tableau.root.preorderIterator<TableauNode>().let { iter ->
            while (iter.hasNext()) {
                iter.next().let { node ->
                    if (checkClosed(node)) {
                        iter.skipMode = true
                    } else {
                        iter.skipMode = false
                    }
                }
            }
        }
    }

    @Suppress("USELESS_CAST")
    fun checkClosed(node: TableauNode): Boolean =
            with(node) {
                if (isClosed())
                    return true
                // TODO might want to cache these or make them easier to access
                val pos: MutableList<SignedFormula<*>> = mutableListOf()
                val neg: MutableList<SignedFormula<*>> = mutableListOf()
                allFormulas.forEach {
                    if (it.sign) {
                        if (it is ClosingFormula) {
                            branchClosers.add(PropositionalBranchCloser(pos = it))
                            return true
                        } else if (it.formula is PropositionalVariable) {
                            pos.add(it) // could short-circuit this by searching here
                        }
                    } else if (it is ClosingFormula) {
                        branchClosers.add(PropositionalBranchCloser(neg = it))
                        return true
                    } else if (it.formula is PropositionalVariable) {
                        neg.add(it) // could short-circuit this by searching here
                    }
                }
                return pos.any { p ->
                    neg.any {
                        (it.formula === p.formula).apply {
                            if (this)
                                branchClosers.add(PropositionalBranchCloser(p, it))
                        }
                    }
                }
            }

}

interface InProgressTableauClosedIndicator {
    fun createExtension(closer: BranchCloser): InProgressTableauClosedIndicator
    val branchClosers: List<BranchCloser>

    /**
     * Throws an exception if this is a closer or NotCompatible
     */
    fun nextNode(): TableauNode

    fun progress(): InProgressTableauClosedIndicator

    fun isCloser(): Boolean
}

/**
 * It's appearance indicates that an attempt was made to extend an InProgressTableauClosedIndicator with a BranchCloser with which it was not compatible.
 */
object NotCompatible : InProgressTableauClosedIndicator {
    override fun progress() = this

    override fun nextNode() = throw IllegalStateException()

    override val branchClosers: List<BranchCloser> = emptyList()

    override fun createExtension(closer: BranchCloser) = this

    override fun isCloser() = false
}

open class BooleanClosedIndicator private constructor(
        branchClosers: List<BranchCloser>
) : InProgressTableauClosedIndicator {

    private constructor(
            branchClosers: List<BranchCloser>,
            nodes: Stack<TableauNode>
    ) : this(branchClosers) {
        @Suppress("UNCHECKED_CAST")
        needToClose = (nodes.clone() as Stack<TableauNode>).apply {
            // NB
            pop()
        }
    }

    constructor(
            node: TableauNode,
            branchClosers: List<BranchCloser> = emptyList()
    ) : this(branchClosers) {
        needToClose = Stack<TableauNode>().apply {
            push(node)
        }
    }

    override val branchClosers: List<BranchCloser> =
            branchClosers

    private lateinit var needToClose: Stack<TableauNode>

    override fun isCloser() =
            needToClose.isEmpty()

    override fun nextNode(): TableauNode =
            needToClose.peek()

    override fun createExtension(closer: BranchCloser): InProgressTableauClosedIndicator {
        return BooleanClosedIndicator(branchClosers + closer, needToClose)
    }

    override fun progress(): InProgressTableauClosedIndicator =
            @Suppress("UNCHECKED_CAST")
            (nextNode().children as List<TableauNode>).takeIf {
                it.isNotEmpty()
            }?.reversed()?.let {
                (needToClose.clone() as Stack<TableauNode>).apply {
                    val orig = pop()
                    it.forEach {
                        push(it)
                    }
                    push(orig) // pushing this back on so that it will be popped by the constructor
                }.let { newNeeds ->
                    BooleanClosedIndicator(branchClosers, newNeeds)
                }
            } ?: NotCompatible

}

//open class FolUnificationClosingStrategy : ClosingStrategy {
//    override fun checkClosed(node: TableauNode): MultiBranchCloser {
//        with(node) {
//            // TODO might want to cache these or make them easier to access
//            val pos: MutableList<PropositionalVariable> = mutableListOf()
//            val neg: MutableList<PropositionalVariable> = mutableListOf()
//            allFormulas.forEach {
//                if (it.formula is PropositionalVariable) {
//                    if (it.sign)
//                        pos.add(it.formula as PropositionalVariable)
//                    else
//                        neg.add(it.formula as PropositionalVariable)
//                } else if (it is ClosingFormula) {
//                    return Closed
//                }
//            }
//            pos.any { p -> neg.any { it === p } }.takeIf { it }?.let { Closed } ?: NotClosed
//        }
//    }
//
//}
