package com.benjishults.bitnots.util.meter

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.step.StepMeterRegistry
import io.micrometer.core.instrument.step.StepRegistryConfig
import java.util.concurrent.TimeUnit

class NoOpPushMeterRegistry(config: StepRegistryConfig, clock: Clock) : StepMeterRegistry(config, clock) {

    override fun publish() {
    }

    override fun getBaseTimeUnit(): TimeUnit {
        return TimeUnit.MILLISECONDS;
    }

}
