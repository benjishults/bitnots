package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.prover.TimedHarness
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.strategy.FolStepStrategy
import com.benjishults.bitnots.tableau.strategy.FolUnificationClosingStrategy
import com.benjishults.bitnots.util.identity.CommitIdTimeVersioner
import com.benjishults.bitnots.util.identity.Versioned
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class FolTableauHarness(
    val qLimit: Long = 3L,
    val stepLimit: Long = -1L,
    override val limitMillis: Long = -1L
) : TimedHarness<FolTableau, FolFormulaTableauProver>, Versioned by CommitIdTimeVersioner {

    override val prover: FolFormulaTableauProver = FolFormulaTableauProver(FolUnificationClosingStrategy(), FolStepStrategy(qLimit))

    override suspend fun rein(proofInProgress: FolTableau): Boolean {
        return stepLimit >= 0 && proofInProgress.steps >= stepLimit
    }

    override fun initializeProof(formula: Formula): FolTableau =
        FolTableau(formula)

    override fun toString(): String =
        sequenceOf(
            limitMillis.takeIf { it >= 0 }
                ?.div(1000.0)
                ?.let { num ->
                    "to=${BigDecimal(num, MathContext(4, RoundingMode.HALF_UP))}"
                } ?: "",
            qLimit.takeIf { it >= 0 }?.let { nonNegative -> "q=$nonNegative" } ?: "",
            stepLimit.takeIf { it >= 0 }?.let { nonNegative -> "sl=$nonNegative" } ?: ""
        ).joinToString(" ").trim()

}
