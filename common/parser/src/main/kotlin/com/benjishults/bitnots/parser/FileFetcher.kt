package com.benjishults.bitnots.parser

import com.benjishults.bitnots.theory.DomainCategory
import com.benjishults.bitnots.theory.formula.FormulaForm
import java.nio.file.Path

interface FileFetcher<in D : DomainCategory, F : FormulaForm, FD : FileDescriptor<F, *>> {

    fun findProblemFolder(domain: D): Path

    suspend fun findAllPaths(
        domain: D,
        form: F
    ): List<Path>

    fun findAllDescriptors(
        domain: D,
        form: F
    ): List<FD>

    fun findProblemFile(descriptor: FD): Path

    suspend fun problemFileFilter(
        domains: List<D>,
        forms: List<F>,
        vararg excludes: FD
    ): List<FD>

}
