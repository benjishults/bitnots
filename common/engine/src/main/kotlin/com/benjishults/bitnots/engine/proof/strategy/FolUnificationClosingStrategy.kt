package com.benjishults.bitnots.engine.proof.strategy

import com.benjishults.bitnots.engine.proof.Tableau
import com.benjishults.bitnots.engine.proof.TableauNode
import com.benjishults.bitnots.engine.proof.closer.UnifyingBranchCloser
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotUnifiable
import com.benjishults.bitnots.theory.formula.SignedFormula
import com.benjishults.bitnots.theory.formula.SimpleSignedFormula

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
