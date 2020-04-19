package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.prover.TimeCommitIdVersionLabelProvider
import com.benjishults.bitnots.prover.TimedHarness
import com.benjishults.bitnots.tableau.PropositionalTableau
import com.benjishults.bitnots.tableau.strategy.PropositionalClosingStrategy
import com.benjishults.bitnots.tableau.strategy.PropositionalStepStrategy
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class PropositionalTableauHarness(
    val stepLimit: Long = -1L,
    override val limitMillis: Long = -1L
) : TimedHarness<PropositionalTableau, PropositionalFormulaProver> {

    override fun rein(proofInProgress: PropositionalTableau): Boolean {
        return stepLimit >= 0 && proofInProgress.getSteps() >= stepLimit
    }

    override val prover: PropositionalFormulaProver = PropositionalFormulaProver(
        PropositionalClosingStrategy(),
        PropositionalStepStrategy(),
        TimeCommitIdVersionLabelProvider.versionLabel()
    )

    override fun initializeProof(formula: Formula): PropositionalTableau =
        PropositionalTableau(formula)

    override fun toString(): String =
        sequenceOf(
            limitMillis.takeIf { it >= 0 }
                ?.div(1000.0)
                ?.let { num ->
                    BigDecimal(num, MathContext(2, RoundingMode.HALF_UP)).toString()
                } ?: "",
            stepLimit.takeIf { it >= 0 }?.let { nonNegative -> "sl=$nonNegative" } ?: ""
        ).joinToString(" ").trim()

}
