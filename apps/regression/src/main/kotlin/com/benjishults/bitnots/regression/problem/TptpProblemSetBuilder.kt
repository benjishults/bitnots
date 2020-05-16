package com.benjishults.bitnots.regression.problem

import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.tptp.TptpFileRepo
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm

data class TptpProblemSetBuilder(
    val name: String,
    val domains: List<TptpDomain>,
    val form: TptpFormulaForm,
    val defaultHarness: FolTableauHarness = FolTableauHarness()
) {

    suspend fun build(): ProblemFileSet<TptpFileRepo> =
        ProblemFileSet(
            name,
            domains.flatMap { domain ->
                TptpFileFetcher.findAllDescriptors(domain, form)
            }.map { fileDescriptor ->
                ProblemFileSetRow(
                    fileDescriptor,
                    defaultHarness
                )
            }
        )
}

