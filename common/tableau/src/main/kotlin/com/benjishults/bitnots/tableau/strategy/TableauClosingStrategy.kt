package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.prover.strategy.FinishingStrategy
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.tableau.closer.InProgressTableauProgressIndicator
import com.benjishults.bitnots.tableau.closer.RanOutOfRunwayTableauProgressIndicator
import com.benjishults.bitnots.tableau.closer.TableauProofProgressIndicator
import com.benjishults.bitnots.util.collection.pop
import com.benjishults.bitnots.util.collection.push

interface TableauClosingStrategy<in T : Tableau<*>> :
        FinishingStrategy<T, TableauProofProgressIndicator> {

    /**
     * Push only if it is InProgressTableauClosedIndicator.
     */
    fun MutableList<InProgressTableauProgressIndicator>.safePush(item: TableauProofProgressIndicator) =
            item is InProgressTableauProgressIndicator && push(item)

    /**
     * This is used to create the initial, top-level indicator before anything is known.
     */
    fun initialProgressIndicatorFactory(tableauNode: TableauNode<*>) : InProgressTableauProgressIndicator

    /**
     * This finds and stores closers on every branch, if possible.
     */
    fun populateBranchClosers(tableau: T)

    /**
     * Calls [populateBranchClosers] then tries to find a compatible combination of branch closers.
     * @return the result of the search for a compatible combination of branch closers.
     */
    override fun checkProgress(proofInProgress: T): TableauProofProgressIndicator {
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

}
