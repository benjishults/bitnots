package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.SimpleSignedFormula
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotCompatible
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.tableau.closer.BranchCloser
import com.benjishults.bitnots.tableau.closer.InProgressTableauProgressIndicator
import com.benjishults.bitnots.tableau.closer.UnifyingProgressIndicator
import com.benjishults.bitnots.util.identity.CommitIdTimeVersioner
import com.benjishults.bitnots.util.identity.Versioned

open class FolUnificationClosingStrategy : TableauClosingStrategy<FolTableau>, Versioned by CommitIdTimeVersioner {

    override fun populateBranchClosers(tableau: FolTableau) {
        with(tableau.root.preorderIterator()) {
            while (hasNext()) {
                generateClosers(next())
            }
        }
    }

    private fun generateClosers(node: TableauNode<*>) {
        if (node.branchClosers.isNotEmpty()) {
            return
        } else {
            // TODO might want to cache these or make them easier to access
            val posAboveOrHere: MutableList<SignedFormula<*>> = mutableListOf()
            val negAboveOrHere: MutableList<SignedFormula<*>> = mutableListOf()
            node.newFormulas.filterIsInstance<SimpleSignedFormula<*>>().also { newSimpleFormulasHere ->
                (node.simpleFormulasAbove + newSimpleFormulasHere).forEach {
                    when {
                        it is ClosingFormula -> {
                            node.branchClosers.add(BranchCloser(
                                    it.takeIf { it.sign },
                                    it.takeIf { !it.sign },
                                    EmptySub))
                            return
                        }
                        it.sign              -> {
                            posAboveOrHere.add(it)
                        }
                        else                 -> {
                            negAboveOrHere.add(it)
                        }
                    }
                }
            }.forEach { f ->
                if (f.sign) {
                    negAboveOrHere.forEach {
                        Formula.unify(it.formula, f.formula, EmptySub).let { theta ->
                            if (theta !== NotCompatible) {
                                node.branchClosers.add(BranchCloser(f, it, theta))
                            }
                        }
                    }
                } else {
                    posAboveOrHere.forEach {
                        Formula.unify(it.formula, f.formula, EmptySub).let { theta ->
                            if (theta !== NotCompatible) {
                                node.branchClosers.add(BranchCloser(it, f, theta))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun initialProgressIndicatorFactory(tableauNode: TableauNode<*>): InProgressTableauProgressIndicator =
            UnifyingProgressIndicator(tableauNode)

}
