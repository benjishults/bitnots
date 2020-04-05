package com.benjishults.bitnots.tableauProver

import com.benjishults.bitnots.model.formulas.Formula
import com.benjishults.bitnots.prover.TimeCommitIdVersionLabelProvider
import com.benjishults.bitnots.prover.TimedHarness
import com.benjishults.bitnots.prover.VersionLabelProvider
import com.benjishults.bitnots.tableau.FolTableau
import com.benjishults.bitnots.tableau.strategy.FolStepStrategy
import com.benjishults.bitnots.tableau.strategy.FolUnificationClosingStrategy
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.Instant
import java.util.*

class FolTableauHarness(
    val qLimit: Long = 3L,
    val stepLimit: Long = -1L,
    override val limitMillis: Long = -1L,
    versionLabelProvider: VersionLabelProvider = TimeCommitIdVersionLabelProvider
) : TimedHarness<FolTableau, FolFormulaTableauProver> {

    override val prover: FolFormulaTableauProver = FolFormulaTableauProver(FolUnificationClosingStrategy(), FolStepStrategy(qLimit), versionLabelProvider.versionLabel())

    override fun rein(proofInProgress: FolTableau): Boolean {
        return stepLimit >= 0 && proofInProgress.getSteps() == stepLimit
    }

    override fun initializeProof(formula: Formula): FolTableau =
        FolTableau(formula)

    private fun versionLabel() = "${gitCommitTime()}__${gitCommitId()}"

    private fun gitCommitId() =
        gitCommitInfo()["git.commit.id"]?.let { it as String } ?: "dirty-SNAPSHOT"

    private fun gitCommitTime(): Instant? =
        gitCommitInfo()["git.commit.time"]?.let { timeString ->
            Instant.parse(timeString as String)
        } ?: Instant.now()

    private fun gitCommitInfo() =
        javaClass.classLoader.getResource("git.properties")?.let { url ->
            url.file
                .reader()
                .buffered()
                .use { reader ->
                    Properties().also {
                        it.load(reader)
                    }
                }
        } ?: Properties()



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
