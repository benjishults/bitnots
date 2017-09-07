package com.benjishults.bitnots.engine.proof

import com.benjishults.bitnots.inference.rules.AlphaFormula
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.model.util.TreeNode
import com.benjishults.bitnots.model.util.TreeNodeImpl

interface TableauNode {

    val newFormulas: MutableList<SignedFormula<Formula<*>>>
    fun isClosed(): Boolean

}

open class PropositionalTableauNode(
        override val newFormulas: MutableList<SignedFormula<Formula<*>>> = mutableListOf(),
        parent: PropositionalTableauNode?
) : TreeNode by TreeNodeImpl(parent), TableauNode {

    var closed: Boolean = newFormulas.any { it is ClosingFormula } || hasCriticalPair()

    // starts as proper ancestors and new ones are added after processing
    // TODO see if I can get rid of this or improve it with shared structure
    val allFormulas = mutableListOf<SignedFormula<Formula<*>>>().also { list ->
        parent?.toAncestors<PropositionalTableauNode> { node ->
            list.addAll(node.newFormulas.filter {
                it is SimpleSignedFormula<*>
            })
        }
    }
    val initialClosers by lazy {
        mutableListOf<Substitution>()
    }

    init {
        applyAllAlphas()
        applyRegularity()
        generateClosers()
        allFormulas.addAll(newFormulas)
    }

    override fun isClosed(): Boolean {
        if (closed ||
                (children.isNotEmpty() && children.all {
                    (it as PropositionalTableauNode).isClosed()
                })) {
            closed = true
            return true
        } else
            return false
    }

    @Suppress("USELESS_CAST")
    fun hasCriticalPair(): Boolean {
        val pos: MutableList<PropositionalVariable> = mutableListOf()
        val neg: MutableList<PropositionalVariable> = mutableListOf()
        allFormulas.map {
            if (it.formula is PropositionalVariable) {
                if (it.sign)
                    pos.add(it.formula as PropositionalVariable)
                else
                    neg.add(it.formula as PropositionalVariable)
            }
        }
        return pos.any { p -> neg.any { it === p } }
    }

    // TODO refactor to get more behaviors out of this class
    private fun generateClosers() {
        allFormulas.filter {
            it.sign
        }.forEach { above ->
            newFormulas.filter {
                !it.sign
            }.forEach {
                above.formula.unify(it.formula).let {
                    initialClosers.add(it)
                }
            }
        }
    }

    private fun applyAllAlphas() {
        while (true) {
            val toAdd: MutableList<SignedFormula<Formula<*>>> = mutableListOf()
            newFormulas.iterator().let {
                while (it.hasNext()) {
                    val current = it.next()
                    if (current is AlphaFormula) {
                        it.remove()
                        toAdd.addAll(current.generateChildren());
                    }
                }
            }
            if (toAdd.isEmpty())
                break
            else
                newFormulas.addAll(toAdd)
        }
    }

    private fun applyRegularity() {
        newFormulas.iterator().let { iter ->
            while (iter.hasNext()) {
                iter.next().let {
                    if (it in allFormulas)
                        iter.remove()
                }
            }
        }
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

open class FolTableauNode(
        newFormulas: MutableList<SignedFormula<Formula<*>>> = mutableListOf(),
        parent: FolTableauNode?
) : PropositionalTableauNode(newFormulas, parent) {

    /**
     * Finds all branch closers at every descendant of the receiver and stores them at the highest node at which all formulas involved occur.
     */
    fun findAllClosers() {

    }

    fun extendMbc() {

    }

}
