package com.benjishults.bitnots.prover.problem

import com.benjishults.bitnots.parser.FileDescriptor
import com.benjishults.bitnots.prover.Harness

data class ProblemRunDescriptor(
    val fileDescriptor: FileDescriptor<*, *>,
    val harness: Harness<*, *>,
    val status: ProblemRunStatus = NotRunStatus
)
