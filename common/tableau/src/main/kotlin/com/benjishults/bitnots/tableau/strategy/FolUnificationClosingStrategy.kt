package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.SimpleSignedFormula
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.model.unifier.EmptySub
import com.benjishults.bitnots.model.unifier.NotCompatible
import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.tableau.closer.BranchCloser
import com.benjishults.bitnots.tableau.closer.InProgressTableauProgressIndicator
import com.benjishults.bitnots.tableau.closer.UnifyingProgressIndicator
import com.benjishults.bitnots.util.identity.CommitIdTimeVersioner
import com.benjishults.bitnots.util.identity.Identified
import com.benjishults.bitnots.util.identity.Versioned

open class FolUnificationClosingStrategy(
    override val criticalPairDetector: CriticalPairDetector<Substitution>
) : TableauClosingStrategy<FolTableau>, Versioned by CommitIdTimeVersioner, Identified by Identified {

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
            val posAboveOrHere: MutableList<PositiveSignedFormula<*>> = mutableListOf()
            val negAboveOrHere: MutableList<NegativeSignedFormula<*>> = mutableListOf()
            node.newFormulas.filterIsInstance<SimpleSignedFormula<*>>().also { newSimpleFormulasHere ->
                (node.simpleFormulasAbove + newSimpleFormulasHere).forEach { signedFormula ->
                    when (signedFormula) {
                        is ClosingFormula        -> {
                            node.branchClosers.add(
                                BranchCloser(
                                    signedFormula.takeIf { it.sign },
                                    signedFormula.takeIf { !it.sign },
                                    EmptySub
                                )
                            )
                            return
                        }
                        is PositiveSignedFormula -> {
                            posAboveOrHere.add(signedFormula)
                        }
                        is NegativeSignedFormula -> {
                            negAboveOrHere.add(signedFormula)
                        }
                    }
                }
            }.forEach { newSignedFormula ->
                when (newSignedFormula) {
                    is PositiveSignedFormula<*> -> {
                        negAboveOrHere.forEach { negativeSignedFormula ->
                            criticalPairDetector.criticalPair(newSignedFormula, negativeSignedFormula).let { theta ->
                                if (theta !== NotCompatible) {
                                    node.branchClosers.add(BranchCloser(newSignedFormula, negativeSignedFormula, theta))
                                }
                            }
                        }
                    }
                    is NegativeSignedFormula<*> -> {
                        posAboveOrHere.forEach { positiveSignedFormula ->
                            criticalPairDetector.criticalPair(positiveSignedFormula, newSignedFormula).let { theta ->
                                if (theta !== NotCompatible) {
                                    node.branchClosers.add(BranchCloser(positiveSignedFormula, newSignedFormula, theta))
                                }
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
