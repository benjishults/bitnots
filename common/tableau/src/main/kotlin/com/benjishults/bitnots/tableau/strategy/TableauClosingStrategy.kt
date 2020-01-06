package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.prover.strategy.FinishingStrategy
import com.benjishults.bitnots.tableau.Tableau
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.tableau.closer.ExtensionFailed
import com.benjishults.bitnots.tableau.closer.InProgressTableauClosedIndicator
import com.benjishults.bitnots.tableau.closer.TableauProofProgressIndicator
import com.benjishults.bitnots.util.collection.pop
import com.benjishults.bitnots.util.collection.push

interface TableauClosingStrategy<in T : Tableau<*>> :
        FinishingStrategy<T, TableauProofProgressIndicator> {

    /**
     * Push only if it is InProgressTableauClosedIndicator.
     */
    fun MutableList<InProgressTableauClosedIndicator>.safePush(item: TableauProofProgressIndicator) {
        if (item is InProgressTableauClosedIndicator)
            push(item)
    }

    /**
     * This is used to create the initial, top-level indicator before anything is known.
     */
    val closedIndicatorFactory: (TableauNode<*>) -> InProgressTableauClosedIndicator

    /**
     * This finds and stores closers on every branch, if possible.
     */
    fun populateBranchClosers(tableau: T)

    /**
     * Calls [populateBranchClosers] then tries to find a compatible combination of branch closers.
     * @return the result of the search for a compatible combination of branch closers.
     */
    override fun searchForClosure(proofInProgress: T): TableauProofProgressIndicator {
        // TODO could this indicate that not all branches have branch-closers?
        populateBranchClosers(proofInProgress)
        // will this ever exceed size 1?
        val toBeExtended = mutableListOf<InProgressTableauClosedIndicator>().also {
            it.push(closedIndicatorFactory(proofInProgress.root))
        }
        do {
            toBeExtended.pop().let { extending ->
                extending.nextNode().branchClosers.forEach { bc ->
                    extending.createExtension(bc).let { ext ->
                        if (ext.isDone()) {
                            return ext
                        } else {
                            toBeExtended.safePush(ext) // this means I'll come back to it
                        }
                    }
                }
                toBeExtended.safePush(extending.progress())
            }
        } while (toBeExtended.isNotEmpty());
        return ExtensionFailed

    }

}
