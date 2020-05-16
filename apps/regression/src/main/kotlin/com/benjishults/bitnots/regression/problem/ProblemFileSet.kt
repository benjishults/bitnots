package com.benjishults.bitnots.regression.problem

import com.benjishults.bitnots.parser.FileDescriptor
import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.prover.problem.NotRunStatus
import com.benjishults.bitnots.prover.problem.ProblemRunStatus
import com.benjishults.bitnots.util.file.isValidFileName
import java.time.Instant

class ProblemFileSet<S : ProblemSource>(
    val name: String,
    val problemFiles: List<ProblemFileSetRow<*, S>>
) {

    // treat this like a Stack
    val runStack: MutableList<ProblemSetRun<S>> = mutableListOf()

    init {
        require(name.isValidFileName()) { "Invalid characters found in problem set name: $name" }
    }

    companion object {
        val EMPTY: ProblemFileSet<*> =
            ProblemFileSet<ProblemSource>(
                "no problem set selected",
                emptyList()
            )
    }

}

class ProblemSetRun<S : ProblemSource>(
    val problemFileSet: ProblemFileSet<S>,
    val startedAt: Instant,
    val fileDescriptors: FileDescriptor<*, S>,
    val harness: Harness<*, *>,
    val status: ProblemRunStatus = NotRunStatus
)
