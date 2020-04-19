package com.benjishults.bitnots.prover

import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.theory.DomainCategory
import com.benjishults.bitnots.theory.formula.FormulaForm
import com.benjishults.bitnots.util.meter.NoOpPushMeterRegistry
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.Timer
import io.micrometer.core.instrument.step.StepRegistryConfig

object ProblemMeterRegistry : NoOpPushMeterRegistry(object : StepRegistryConfig {

    override fun get(key: String): String? {
        return null
    }

    override fun prefix(): String = ""

}, Clock.SYSTEM) {

    fun fetchTimer(
        domain: DomainCategory,
        source: ProblemSource,
        form: FormulaForm,
        number: Long = -1L,
        version: String = "",
        size: Long = -1L
    ): Timer =
        ProblemMeterRegistry.timer(
            "problem",
            "domain", domain.abbreviation,
            "source", source.abbreviation,
            "form", form.abbreviation,
            "number", number.toString(),
            "version", version,
            "size", size.toString()
        )

}
