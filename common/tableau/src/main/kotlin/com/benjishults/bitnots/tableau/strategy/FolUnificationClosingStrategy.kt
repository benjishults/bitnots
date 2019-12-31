package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.SimpleSignedFormula
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.tableau.closer.InProgressTableauClosedIndicator
import com.benjishults.bitnots.tableau.closer.UnifyingBranchCloser
import com.benjishults.bitnots.tableau.closer.UnifyingClosedIndicator

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

    override fun tryFinish(proofInProgress: Tableau): InProgressTableauClosedIndicator {
        populateBranchClosers(proofInProgress)
        return UnifyingClosedIndicator(proofInProgress.root)
    }

    private fun generateClosers(node: TableauNode) {
        with(node) {
            if (branchClosers.isNotEmpty()) {
                return
            } else {
                // TODO might want to cache these or make them easier to access
                val posAbove: MutableList<SignedFormula<*>> = mutableListOf()
                val negAbove: MutableList<SignedFormula<*>> = mutableListOf()
                newFormulas.filterIsInstance<SimpleSignedFormula<*>>().also { newbies ->
                    (simpleFormulasAbove + newbies).forEach {
                        if (it.sign) {
                            if (it is ClosingFormula) {
                                branchClosers.add(UnifyingBranchCloser(it, null, EmptySub))
                                return
                            } else {
                                posAbove.add(it)
                            }
                        } else if (it is ClosingFormula) {
                            branchClosers.add(UnifyingBranchCloser(null, it, EmptySub))
                            return
                        } else {
                            negAbove.add(it)
                        }
                    }
                }.forEach { f ->
                    if (f.sign) {
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
