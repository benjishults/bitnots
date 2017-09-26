package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.inference.rules.SignedFormula
import com.benjishults.bitnots.inference.rules.SimpleSignedFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotUnifiable

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
                val posAbove: MutableList<SignedFormula<*>> = mutableListOf()
                val negAbove: MutableList<SignedFormula<*>> = mutableListOf()
                newFormulas.filterIsInstance<SimpleSignedFormula<*>>().also { newbies ->
                    (simpleFormulasAbove + newbies).forEach {
                        if (it.sign) {
                            if (it is ClosingFormula) {
                                branchClosers.add(PropositionalBranchCloser(pos = it))
                                return true
                            } else if (it.formula is PropositionalVariable) {
                                posAbove.add(it) // could short-circuit this by searching here
                            }
                        } else if (it is ClosingFormula) {
                            branchClosers.add(PropositionalBranchCloser(neg = it))
                            return true
                        } else if (it.formula is PropositionalVariable) {
                            negAbove.add(it) // could short-circuit this by searching here
                        }
                    }
                }.any { f ->
                    if (f.sign) {
                        if (f is ClosingFormula) {
                            branchClosers.add(PropositionalBranchCloser(pos = f))
                            true
                        } else
                            negAbove.any {
                                (it.formula === f.formula).apply {
                                    if (this)
                                        branchClosers.add(PropositionalBranchCloser(f, it))
                                }
                            }
                    } else {
                        if (f is ClosingFormula) {
                            branchClosers.add(PropositionalBranchCloser(neg = f))
                            true
                        } else
                            posAbove.any {
                                (it.formula === f.formula).apply {
                                    if (this)
                                        branchClosers.add(PropositionalBranchCloser(it, f))
                                }
                            }
                    }
                }
            }

}

open class FolUnificationClosingStrategy : TableauClosingStrategy {

    override fun populateBranchClosers(tableau: Tableau) {
        tableau.root.preorderIterator<TableauNode>().let { iter ->
            while (iter.hasNext()) {
                iter.next().let {
                    generateClosers(it)
                }
            }
        }
    }

    fun generateClosers(node: TableauNode) {
        with(node) {
            // TODO might want to cache these or make them easier to access
            val posAbove: MutableList<SignedFormula<*>> = mutableListOf()
            val negAbove: MutableList<SignedFormula<*>> = mutableListOf()
            simpleFormulasAbove.forEach {
                if (it.sign) {
                    if (it is ClosingFormula) {
                        branchClosers.add(PropositionalBranchCloser(pos = it))
                        return
                    } else if (it.formula is PropositionalVariable) {
                        posAbove.add(it) // could short-circuit this by searching here
                    }
                } else if (it is ClosingFormula) {
                    branchClosers.add(PropositionalBranchCloser(neg = it))
                    return
                } else if (it.formula is PropositionalVariable) {
                    negAbove.add(it) // could short-circuit this by searching here
                }
            }
            newFormulas.forEach { f ->
                if (f is SimpleSignedFormula<*>) {
                    if (f.sign) {
                        // TODO duplicate code
                        negAbove.forEach {
                            Formula.unify(it.formula, f.formula, EmptySub).let { theta ->
                                if (theta !== NotUnifiable) {
                                    branchClosers.add(UnifyingBranchCloser(f, it, theta))
                                }
                            }
                        }
                    } else {
                        posAbove.forEach {
                            Formula.unify(it.formula, f.formula, EmptySub).let { theta ->
                                if (theta !== NotUnifiable) {
                                    branchClosers.add(UnifyingBranchCloser(it, f, theta))
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}
