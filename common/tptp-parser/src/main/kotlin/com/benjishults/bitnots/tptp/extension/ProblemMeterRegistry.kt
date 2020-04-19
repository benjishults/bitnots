package com.benjishults.bitnots.tptp.extension

import com.benjishults.bitnots.prover.ProblemMeterRegistry
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import io.micrometer.core.instrument.Timer

fun ProblemMeterRegistry.fetchTimer(descriptor: TptpProblemFileDescriptor): Timer = ProblemMeterRegistry.fetchTimer(
    descriptor.domain,
    descriptor.source,
    descriptor.form,
    descriptor.number,
    descriptor.version.toString(),
    descriptor.size
)
