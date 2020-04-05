package com.benjishults.bitnots.regression.app.problem

import com.benjishults.bitnots.parser.FileDescriptor
import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.tableauProver.PropositionalTableauHarness
import com.benjishults.bitnots.theory.DomainCategory
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import java.nio.file.Path
import java.time.Instant


class ProblemSet(
    val name: String,
    // vararg val problems: FileDescriptor,
    val harnesses: Map<FileDescriptor, Harness<*>>
) {

    val path: Path
    val problems = harnesses.keys

    // treat this like a Stack
    val history: MutableList<ProblemSetRun> = mutableListOf()

    init {
        path = nameToPath()
    }

    private fun nameToPath(): Path {
        return Path.of("")
    }

    companion object {
        val EMPTY = ProblemSet("No problem set selected", harnesses = emptyMap())
    }

}

data class ProblemSetBuilder(
    val name: String,
    val format: ProblemSource,
    val domains: List<DomainCategory>,
    val form: TptpFormulaForm
) {

    val harnesses: MutableMap<FileDescriptor, Harness<*>> = mutableMapOf()

    fun build() = ProblemSet(
        name,
        domains.flatMap { domain ->
            TptpFileFetcher.findAllDescriptors(domain as TptpDomain, form)
        }.associateWith<FileDescriptor, Harness<*>> {
            when (it.form) {
                TptpFormulaForm.FOF -> FolTableauHarness()
                TptpFormulaForm.CNF -> PropositionalTableauHarness()
                else                -> error("Unknown formula form")
            }
        }
    )

}

class ProblemSetRun(
    val problemSet: ProblemSet,
    val startedAt: Instant,
    vararg val problemRunDescriptors: ProblemRunDescriptor
)

