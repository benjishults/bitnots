package com.benjishults.bitnots.regression.problem

import com.benjishults.bitnots.parser.FileDescriptor
import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.prover.Harness
import com.benjishults.bitnots.prover.problem.NotRunStatus
import com.benjishults.bitnots.prover.problem.ProblemRunStatus
import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.tptp.TptpFileRepo
import com.benjishults.bitnots.tptp.files.TptpDomain
import com.benjishults.bitnots.tptp.files.TptpFormulaForm
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import com.benjishults.bitnots.util.file.isValidFileName
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Path
import java.time.Instant
import kotlin.reflect.KClass

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
        val sourceToTypeMap: Map<String, KClass<*>> = mapOf("TPTP" to TptpFileRepo::class)
        private val objectMapper: ObjectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
        val EMPTY: ProblemFileSet<*> =
            ProblemFileSet<ProblemSource>(
                "no problem set selected",
                emptyList()
            )

        fun <T : ProblemSource> fromPath(path: Path): ProblemFileSet<T> {
            TODO()
            path.toFile().inputStream().use { yamlFile ->
                objectMapper.readTree(yamlFile).also { yaml ->
                    val problems = yaml["problems"]
                    val source = yaml["source"].toString()
                    val problemFiles = when (sourceToTypeMap[source]) {
                        TptpFileRepo::class -> {
                            yaml["problems"].map { problem ->
                                ProblemFileSetRow(
                                    TptpProblemFileDescriptor(
                                        TptpDomain.valueOf(problem["domain"].toString()),
                                        TptpFormulaForm.abbreviationToInstanceMap[problem["form"].toString()]!!,
                                        problem["number"].takeUnless { it is NullNode }?.asLong() ?: -1,
                                        problem["version"].takeUnless { it is NullNode }?.asLong() ?: -1,
                                        problem["size"].takeUnless { it is NullNode }?.asLong() ?: -1
                                    ), FolTableauHarness()
                                )

                            }
                            // ProblemFileSet<TptpFileRepo>(yaml["name"].toString(), problems.map { problem ->
                            //
                            // })
                        }
                        else                -> TODO("Not implemented")
                    }
                }
            }
        }
    }

}
// @ExperimentalCoroutinesApi
// class TptpProblemFileSet(
//     name: String,
//     val domains: List<TptpDomain>,
//     val form: TptpFormulaForm,
//     val defaultHarness: FolTableauHarness = FolTableauHarness()
// ) : ProblemFileSet<TptpFileRepo>(
//     name,
//     mutableListOf<ProblemFileSetRow<*, TptpFileRepo>>().also { list ->
//         runBlocking {
//             domains.forEach { domain ->
//                 launch(Dispatchers.IO) {
//                     TptpFileFetcher.findAllDescriptors(domain, form).onEach { fileDescriptor ->
//                         list.add(
//                             ProblemFileSetRow(
//                                 fileDescriptor,
//                                 defaultHarness
//                             )
//                         )
//                     }
//                 }
//             }
//
//         }
//     }
// )

class ProblemSetRun<S : ProblemSource>(
    val problemFileSet: ProblemFileSet<S>,
    val startedAt: Instant,
    val fileDescriptors: FileDescriptor<*, S>,
    val harness: Harness<*, *>,
    val status: ProblemRunStatus = NotRunStatus
)
