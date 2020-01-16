package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.inference.SignedFormula
import com.benjishults.bitnots.inference.SimpleSignedFormula
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.model.formulas.propositional.PropositionalVariable
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.tableau.closer.BranchCloser
import com.benjishults.bitnots.tableau.closer.InProgressTableauClosedIndicator

open class PropositionalClosingStrategy(
        override val closedIndicatorFactory: (TableauNode<*>) -> InProgressTableauClosedIndicator
) : TableauClosingStrategy<PropositionalTableau> {

    /**
     *
     */
    override fun populateBranchClosers(tableau: PropositionalTableau) {
        with(tableau.root.preorderIterator()) {
            while (hasNext()) {
                skipMode = checkClosed(next())
            }
        }
    }

    @Suppress("USELESS_CAST")
    private fun checkClosed(node: TableauNode<*>): Boolean =
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
                                branchClosers.add(BranchCloser(pos = it))
                                return true
                            } else if (it.formula is PropositionalVariable) {
                                posAbove.add(it) // could short-circuit this by searching here
                            }
                        } else if (it is ClosingFormula) {
                            branchClosers.add(BranchCloser(neg = it))
                            return true
                        } else if (it.formula is PropositionalVariable) {
                            negAbove.add(it) // could short-circuit this by searching here
                        }
                    }
                }.any { f ->
                    if (f.sign) {
                        negAbove.any {
                            (it.formula == f.formula).apply {
                                if (this)
                                    branchClosers.add(BranchCloser(f, it))
                            }
                        }
                    } else {
                        posAbove.any {
                            (it.formula == f.formula).apply {
                                if (this)
                                    branchClosers.add(BranchCloser(it, f))
                            }
                        }
                    }
                }
            }

}
