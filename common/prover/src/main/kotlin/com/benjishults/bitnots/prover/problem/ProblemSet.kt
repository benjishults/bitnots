package com.benjishults.bitnots.prover.problem

import java.time.Instant

class ProblemSet(
    val name: String,
    val problems: List<ProblemRunDescriptor>
) {

    // treat this like a Stack
    val history: MutableList<ProblemSetRun> = mutableListOf()

    companion object {
        val EMPTY : ProblemSet =
            ProblemSet("no problem set selected", emptyList())
    }

}

class ProblemSetRun(
    val problemSet: ProblemSet,
    val startedAt: Instant,
    vararg val problemRunDescriptors: ProblemRunDescriptor
)
