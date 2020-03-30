package com.benjishults.bitnots.regression.app.problem

import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.theory.ProblemDescriptor
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import java.nio.file.Path


class ProblemSet(
        val name: String,
        vararg val problems: ProblemDescriptor,
        val harnesses: Map<ProblemDescriptor, Harness<*, *>>
) {

    val path: Path

    // treat this like a Stack
    val history: MutableList<ProblemSetRun> = mutableListOf()

    init {
        path = nameToPath()
    }

    private fun nameToPath(): Path {
        return Path.of("")
    }

}

data class ProblemSetBuilder(
        val name: String,
        val format: ProblemSource,
        val domains: List<TptpDomain>,
        val form: TptpFormulaForm
) {

    val harnesses: MutableMap<ProblemDescriptor, Harness<*, *>> = mutableMapOf()

    fun build() = ProblemSet(
            name,
            *domains.flatMap { domain ->
                TptpFileFetcher.findAllDescriptors(domain, form)
            }.toTypedArray(),
            harnesses = harnesses)

}

class ProblemSetRun(
        val problemSet: ProblemSet,
        vararg val problemRunDescriptors: ProblemRunDescriptor
)

