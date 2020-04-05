package com.benjishults.bitnots.regression.problem

import com.benjishults.bitnots.prover.problem.ProblemRunDescriptor
import com.benjishults.bitnots.prover.problem.ProblemSet
import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm

data class TptpProblemSetBuilder(
    val name: String,
    val domains: List<TptpDomain>,
    val form: TptpFormulaForm,
    val defaultHarness: FolTableauHarness = FolTableauHarness()
) {

    fun build(): ProblemSet =
        ProblemSet(
            // TODO validate
            name,
            domains.flatMap { domain ->
                TptpFileFetcher.findAllDescriptors(domain, form)
            }.map { fileDescriptor ->
                ProblemRunDescriptor(
                    fileDescriptor,
                    defaultHarness
                )
            }
        )
}

