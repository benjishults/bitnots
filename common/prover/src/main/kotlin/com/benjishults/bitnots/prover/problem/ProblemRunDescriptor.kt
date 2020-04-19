package com.benjishults.bitnots.prover.problem

import com.benjishults.bitnots.parser.FileDescriptor
import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.theory.formula.FormulaForm

data class ProblemRunDescriptor<F: FormulaForm>(
    val fileDescriptor: FileDescriptor<F, *>,
    val harness: Harness<*, *>,
    val status: ProblemRunStatus = NotRunStatus
)
