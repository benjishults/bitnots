package com.benjishults.bitnots.regression.problem

import com.benjishults.bitnots.parser.FileDescriptor
import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.prover.problem.NotRunStatus
import com.benjishults.bitnots.prover.problem.ProblemRunStatus
import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.tptp.TptpFileRepo
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFileFetcher
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.util.file.isValidFileName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant

open class ProblemFileSet<S : ProblemSource>(
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

@ExperimentalCoroutinesApi
class TptpProblemFileSet(
    name: String,
    val domains: List<TptpDomain>,
    val form: TptpFormulaForm,
    val defaultHarness: FolTableauHarness = FolTableauHarness()
) : ProblemFileSet<TptpFileRepo>(
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

class ProblemSetRun<S : ProblemSource>(
    val problemFileSet: ProblemFileSet<S>,
    val startedAt: Instant,
    val fileDescriptors: FileDescriptor<*, S>,
    val harness: Harness<*, *>,
    val status: ProblemRunStatus = NotRunStatus
)
