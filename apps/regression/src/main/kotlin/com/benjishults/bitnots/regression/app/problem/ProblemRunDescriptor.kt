package com.benjishults.bitnots.regression.app.problem

import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.theory.ProblemDescriptor

class ProblemRunDescriptor(
    val prover: ProverDescriptor<*>,
    val problemDescriptor: ProblemDescriptor,
    val harness: Harness<*, *>
) : ProblemDescriptor by problemDescriptor
