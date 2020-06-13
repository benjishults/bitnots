package com.benjishults.bitnots.regression.problem

import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.tptp.TptpFileRepo
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class TptpProblemSetBuilder(
    val name: String,
    val domains: List<TptpDomain>,
    val form: TptpFormulaForm,
    val defaultHarness: FolTableauHarness = FolTableauHarness()
) {

    @ExperimentalCoroutinesApi
    fun buildNew(): ProblemFileSet<TptpFileRepo> =
        ProblemFileSet(
            name,
            mutableListOf<ProblemFileSetRow<*, TptpFileRepo>>().also { list ->
                runBlocking {
                    domains.forEach { domain ->
                        launch(Dispatchers.IO) {
                            TptpFileFetcher.findAllDescriptors(domain, form).onEach { fileDescriptor ->
                                list.add(
                                    ProblemFileSetRow(
                                        fileDescriptor,
                                        defaultHarness
                                    )
                                )
                            }
                        }
                    }
                }
            }
        )
}
