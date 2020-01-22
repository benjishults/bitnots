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

open class FolUnificationClosingStrategy(
        override val progressIndicatorFactory: (TableauNode<*>) -> InProgressTableauProgressIndicator
) : TableauClosingStrategy<FolTableau> {

    override fun populateBranchClosers(tableau: FolTableau) {
        with(tableau.root.preorderIterator()) {
            while (hasNext()) {
                generateClosers(next())
            }
        }
    }

    private fun generateClosers(node: TableauNode<*>) {
        with(node) {
            if (branchClosers.isNotEmpty()) {
                return
            } else {
                // TODO might want to cache these or make them easier to access
                val posAboveOrHere: MutableList<SignedFormula<*>> = mutableListOf()
                val negAboveOrHere: MutableList<SignedFormula<*>> = mutableListOf()
                newFormulas.filterIsInstance<SimpleSignedFormula<*>>().also { newSimpleFormulasHere ->
                    (simpleFormulasAbove + newSimpleFormulasHere).forEach {
                        if (it is ClosingFormula) {
                            branchClosers.add(BranchCloser(null, it, EmptySub))
                            return
                        } else if (it.sign) {
                            posAboveOrHere.add(it)
                        } else {
                            negAboveOrHere.add(it)
                        }
                    }
                }.forEach { f ->
                    if (f.sign) {
                        negAboveOrHere.forEach {
                            Formula.unify(it.formula, f.formula, EmptySub).let { theta ->
                                if (theta !== NotCompatible) {
                                    branchClosers.add(BranchCloser(f, it, theta))
                                }
                            }
                        }
                    } else {
                        posAboveOrHere.forEach {
                            Formula.unify(it.formula, f.formula, EmptySub).let { theta ->
                                if (theta !== NotCompatible) {
                                    branchClosers.add(BranchCloser(it, f, theta))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
