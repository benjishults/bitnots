package com.benjishults.bitnots.tableau.strategy

import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.TableauNode
import com.benjishults.bitnots.tableau.closer.BooleanProgressIndicator
import com.benjishults.bitnots.tableau.closer.InProgressTableauProgressIndicator
import com.benjishults.bitnots.util.AllOrNothing
import com.benjishults.bitnots.util.identity.CommitIdTimeVersioner
import com.benjishults.bitnots.util.identity.Identified
import com.benjishults.bitnots.util.identity.Versioned

open class PropositionalClosingStrategy(
    override val criticalPairDetector: CriticalPairDetector<AllOrNothing>
) : TableauClosingStrategy<PropositionalTableau>, Versioned by CommitIdTimeVersioner, Identified by Identified {

    override fun initialProgressIndicatorFactory(tableauNode: TableauNode<*>): InProgressTableauProgressIndicator =
        BooleanProgressIndicator(tableauNode)

}
