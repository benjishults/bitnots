package com.benjishults.bitnots.regression.problem

import com.benjishults.bitnots.parser.FileDescriptor
import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.prover.problem.NotRunStatus
import com.benjishults.bitnots.prover.problem.ProblemRunStatus
import com.benjishults.bitnots.theory.formula.FormulaForm

/**
 * Row in regression main pane
 */
data class ProblemFileSetRow<F : FormulaForm, S : ProblemSource>(
    val fileDescriptor: FileDescriptor<F, S>,
    val harness: Harness<*, *>,
    val status: ProblemRunStatus = NotRunStatus
)
