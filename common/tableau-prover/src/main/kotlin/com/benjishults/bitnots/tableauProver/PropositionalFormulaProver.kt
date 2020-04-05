package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.prover.Prover
import com.benjishults.bitnots.prover.strategy.StepStrategy
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.strategy.PropositionalClosingStrategy

open class PropositionalFormulaProver(
    override val finishingStrategy: PropositionalClosingStrategy,
    override val stepStrategy: StepStrategy<PropositionalTableau>,
    override val version: String = "unversioned"
) : Prover<PropositionalTableau>
