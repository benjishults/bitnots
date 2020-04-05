package com.benjishults.bitnots.prover.problem

import com.benjishults.bitnots.parser.FileDescriptor
import com.benjishults.bitnots.prover.TimedHarness

data class ProblemRunDescriptor(
    val fileDescriptor: FileDescriptor,
    val harness: TimedHarness<*, *>,
    val status: ProblemRunStatus = NotRunStatus
)
