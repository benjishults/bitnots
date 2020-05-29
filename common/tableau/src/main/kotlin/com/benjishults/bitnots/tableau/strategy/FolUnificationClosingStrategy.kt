package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.model.unifier.Substitution
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.tableau.closer.InProgressTableauProgressIndicator
import com.benjishults.bitnots.tableau.closer.UnifyingProgressIndicator
import com.benjishults.bitnots.util.identity.CommitIdTimeVersioner
import com.benjishults.bitnots.util.identity.Identified
import com.benjishults.bitnots.util.identity.Versioned

open class FolUnificationClosingStrategy(
    override val criticalPairDetector: CriticalPairDetector<Substitution>
) : TableauClosingStrategy<FolTableau>, Versioned by CommitIdTimeVersioner, Identified by Identified {

    override fun initialProgressIndicatorFactory(tableauNode: TableauNode<*>): InProgressTableauProgressIndicator =
        UnifyingProgressIndicator(tableauNode)

}
