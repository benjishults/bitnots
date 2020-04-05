package com.benjishults.bitnots.prover.problem

import com.benjishults.bitnots.prover.finish.FailedProofIndicator
import com.benjishults.bitnots.prover.finish.ProofProgressIndicator
import com.benjishults.bitnots.prover.finish.SuccessfulProofIndicator
import com.benjishults.bitnots.prover.finish.TimeOutProofIndicator
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

sealed class ProblemRunStatus {
    abstract override fun toString(): String
}

fun ProofProgressIndicator.toProblemRunStatus(): ProblemRunStatus =
    when (this) {
        is SuccessfulProofIndicator -> SuccessfulRunStatus
        is TimeOutProofIndicator -> TimeoutRunStatus
        is FailedProofIndicator -> FailedRunStatus(this.toString())
        // is RanOutOfRunwayTableauProgressIndicator -> FailedRunStatus("")
        // is EngineErrorIndicator<*> -> FailedRunStatus("prover threw exception")
        // is ExtensionFailed -> FailedRunStatus("")
        // is FailedProofIndicator -> FailedRunStatus("")
        else -> FailedRunStatus("other")
    }

object NotRunStatus : ProblemRunStatus() {
    override fun toString(): String {
        return ""
    }

}

object SuccessfulRunStatus : ProblemRunStatus() {
    override fun toString(): String =
        "DONE"
}

class SuccessfulTimedRunStatus(val timeMillis: Long) : ProblemRunStatus() {

    override fun toString(): String =
        timeMillis.takeIf { it >= 0 }
            ?.div(1000.0)
            ?.let { num ->
                "SUCCESS=${BigDecimal(num, MathContext(2, RoundingMode.HALF_UP))}"
            } ?: "SUCCESS"

}

class FailedRunStatus(val reason: String) : ProblemRunStatus() {

    override fun toString(): String =
        "FAILED=$reason"

}

object TimeoutRunStatus : ProblemRunStatus() {

    override fun toString(): String =
        "TIMEOUT"

}


