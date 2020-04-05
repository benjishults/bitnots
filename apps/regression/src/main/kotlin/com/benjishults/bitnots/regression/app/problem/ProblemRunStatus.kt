package com.benjishults.bitnots.regression.app.problem

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

sealed class ProblemRunStatus {
    abstract override fun toString(): String
}

object NotRunStatus : ProblemRunStatus() {
    override fun toString(): String {
        return ""
    }

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


