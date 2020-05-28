package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.prover.Prover
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.strategy.FolStepStrategy
import com.benjishults.bitnots.tableau.strategy.FolUnificationClosingStrategy
import com.benjishults.bitnots.util.identity.CommitIdTimeVersioner
import com.benjishults.bitnots.util.identity.Identified
import com.benjishults.bitnots.util.identity.Versioned

class FolFormulaTableauProver(
    override val finishingStrategy: FolUnificationClosingStrategy,
    override val stepStrategy: FolStepStrategy
) : Prover<FolTableau>, Versioned by CommitIdTimeVersioner, Identified by Identified
