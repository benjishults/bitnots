package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.inference.NegativeSignedFormula
import com.benjishults.bitnots.inference.PositiveSignedFormula
import com.benjishults.bitnots.inference.SimpleSignedFormula
import com.benjishults.bitnots.inference.rules.ClosingFormula
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.prover.strategy.FinishingStrategy
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.tableau.closer.BranchCloser
import com.benjishults.bitnots.tableau.closer.InProgressTableauProgressIndicator
import com.benjishults.bitnots.tableau.closer.RanOutOfRunwayTableauProgressIndicator
import com.benjishults.bitnots.util.AllOrNothing
import com.benjishults.bitnots.util.Match
import com.benjishults.bitnots.util.NoMatch
import com.benjishults.bitnots.util.PotentialMatch
import com.benjishults.bitnots.util.collection.pop
import com.benjishults.bitnots.util.collection.push

interface TableauClosingStrategy<in T : Tableau<*>> :
    FinishingStrategy<T, ProofProgressIndicator> {

    val criticalPairDetector: CriticalPairDetector<*>

    /**
     * This finds and stores closers on every branch, if possible.
     */
    fun populateBranchClosers(tableau: T) {
        tableau.root.preorderIterator().let { iterator ->
            while (iterator.hasNext()) {
                iterator.skipMode = (generateClosers(iterator.next()) is Match)
            }
        }
    }

    /**
     * Push only if it is InProgressTableauClosedIndicator.
     */
    fun MutableList<InProgressTableauProgressIndicator>.safePush(item: ProofProgressIndicator) =
        item is InProgressTableauProgressIndicator && push(item)

    /**
     * This is used to create the initial, top-level indicator before anything is known.
     */
    fun initialProgressIndicatorFactory(tableauNode: TableauNode<*>): InProgressTableauProgressIndicator


    /**
     * Calls [populateBranchClosers] then tries to find a compatible combination of branch closers.
     * @return the result of the search for a compatible combination of branch closers.
     */
    override fun checkProgress(proofInProgress: T): ProofProgressIndicator {
        // TODO could this indicate that not all branches have branch-closers?
        populateBranchClosers(proofInProgress)
        // will this ever exceed size 1?
        val toBeExtended = mutableListOf<InProgressTableauProgressIndicator>().also {
            it.push(initialProgressIndicatorFactory(proofInProgress.root))
        }
        do {
            toBeExtended.pop().let { extending ->
                extending.nextNode().branchClosers.forEach { bc ->
                    extending.createExtension(bc).let { ext ->
                        if (ext.isDone()) {
                            proofInProgress.indicator = ext
                            return ext
                        } else {
                            toBeExtended.safePush(ext) // this means I'll come back to it
                        }
                    }
                }
                toBeExtended.safePush(extending.progress())
            }
        } while (toBeExtended.isNotEmpty());
        return RanOutOfRunwayTableauProgressIndicator.also { indicator ->
            proofInProgress.indicator = indicator
        }
    }

    /**
     * Returns NoMatch if all matches found were only potential matches.
     */
    fun generateClosers(node: TableauNode<*>): AllOrNothing {
        node.closer()?.let {
            return it
        }
        // TODO might want to cache these or make them easier to access
        val posAboveOrHere: MutableList<PositiveSignedFormula<*>> = mutableListOf()
        val negAboveOrHere: MutableList<NegativeSignedFormula<*>> = mutableListOf()
        node.newFormulas.filterIsInstance<SimpleSignedFormula<*>>().also { newSimpleFormulasHere ->
            (node.simpleFormulasAbove + newSimpleFormulasHere).forEach { (cons, formList) ->
                when (signedFormula) {
                    is ClosingFormula        -> {
                        node.branchClosers.add(
                            BranchCloser(
                                signedFormula.takeIf { it.sign },
                                signedFormula.takeIf { !it.sign }
                            )
                        )
                        // short-circuit
                        return Match
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
                        checkCriticalPair(newSignedFormula, negativeSignedFormula, node).let {
                            if (it is Match)
                                return it
                        }
                    }
                }
                is NegativeSignedFormula<*> -> {
                    posAboveOrHere.forEach { positiveSignedFormula ->
                        checkCriticalPair(positiveSignedFormula, newSignedFormula, node).let {
                            if (it is Match)
                                return it
                        }
                    }
                }
            }
        }
        return NoMatch
    }

    fun checkCriticalPair(
        positiveSignedFormula: PositiveSignedFormula<*>,
        negativeSignedFormula: NegativeSignedFormula<*>,
        node: TableauNode<*>
    ): Any =
        criticalPairDetector.criticalPair(positiveSignedFormula, negativeSignedFormula).let { theta ->
            when (theta) {
                is Match         -> {
                    node.branchClosers.add(BranchCloser(positiveSignedFormula, negativeSignedFormula))
                    theta
                }
                !is AllOrNothing -> {
                    node.branchClosers.add(
                        BranchCloser(
                            positiveSignedFormula,
                            negativeSignedFormula,
                            theta as PotentialMatch<*, *>
                        )
                    )
                    Unit
                }
                else             -> {
                    // do nothing
                    Unit
                }
            }
        }

}
