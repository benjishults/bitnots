package com.benjishults.bitnots.regression.app.problem

import com.benjishults.bitnots.parser.FileDescriptor
import com.benjishults.bitnots.prover.Harness

class ProblemRunDescriptor(
    val fileDescriptor: FileDescriptor,
    // val problemDescriptor: ProblemDescriptor,
    val harness: Harness<*>,
    val status: ProblemRunStatus = NotRunStatus
)
