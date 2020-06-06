package com.benjishults.bitnots.tptp.files

import com.benjishults.bitnots.parser.FileFetcher
import com.benjishults.bitnots.tptp.TptpProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.regex.Matcher
import java.util.regex.Pattern

fun padToThreeDigits(version: Long) =
    String.format("%03d", version)

object TptpFileFetcher : FileFetcher<TptpDomain, TptpFormulaForm, TptpProblemFileDescriptor> {

    private val FILE_SYSTEM: FileSystem = FileSystems.getDefault()

    override fun findProblemFolder(domain: TptpDomain): Path = FILE_SYSTEM.getPath(
        TptpProperties.getBaseFolderName(),
        "Problems", domain.toString()
    )

    override suspend fun findAllPaths(
        domain: TptpDomain,
        form: TptpFormulaForm
    ): List<Path> = withContext(Dispatchers.IO) {
        val pattern = problemPattern(domain, form)
        return@withContext Files.newDirectoryStream(findProblemFolder(domain)).use { directoryStream ->
            directoryStream.filter {
                pattern.matcher(it.fileName.toString()).matches()
            }.toList()
        }
    }

    override fun findAllDescriptors(
        domain: TptpDomain,
        form: TptpFormulaForm
    ): List<TptpProblemFileDescriptor> =
        mutableListOf<TptpProblemFileDescriptor>().also { list ->
            val pattern = problemPattern(domain, form)
            Files.newDirectoryStream(findProblemFolder(domain)).use { directoryStream ->
                directoryStream
                    .map { path ->
                        pattern.matcher(path.fileName.toString())
                    }
                    .filter(Matcher::matches)
                    .forEach { matcher ->
                        list.add(
                            TptpProblemFileDescriptor(
                                domain,
                                form,
                                matcher.group("number").toLong(10),
                                matcher.group("version").toLong(10),
                                matcher.group("size")?.toLong(10) ?: -1
                            )
                        )
                    }
            }
        }


    // TODO memoize
    private fun problemPattern(
        domain: TptpDomain,
        form: TptpFormulaForm
    ): Pattern {
        return Pattern.compile("${domain}(?<number>[0-9]{3})${form.representation.takeIf { representation ->
            // `+` is interpreted specially in regular expressions
            representation != '+'
        }?.toString() ?: "\\+"}(?<version>[1-9][0-9]*)\\.(?:(?<size>[0-9]+)\\.)?p")
    }

    override fun findProblemFile(descriptor: TptpProblemFileDescriptor): Path {
        return findProblemFolder(descriptor.domain).resolve(descriptor.toFileName())
    }

    override suspend fun problemFileFilter(
        domains: List<TptpDomain>,
        forms: List<TptpFormulaForm>,
        vararg excludes: TptpProblemFileDescriptor
    ): List<TptpProblemFileDescriptor> = withContext(Dispatchers.IO) {
        val base = FILE_SYSTEM.getPath(
            TptpProperties.getBaseFolderName(),
            "Problems"
        )
        val value = mutableListOf<TptpProblemFileDescriptor>()
        domains.forEach { domain ->
            val domainPath = base.resolve(domain.toString())
            Files.walkFileTree(domainPath, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    val descriptor =
                        try {
                            TptpProblemFileDescriptor.fromFileName(file.fileName.toString())
                        } catch (e: IllegalStateException) {
                            return FileVisitResult.CONTINUE
                        }
                    if (descriptor.domain === domain &&
                        forms.contains(descriptor.form) &&
                        !excludes.contains(descriptor)
                    )
                        value.add(descriptor)
                    return FileVisitResult.CONTINUE
                }
            })
        }
        return@withContext value.sortedWith(TptpProblemFileDescriptor.comparator)
    }

    fun findProblemFile(
        domain: TptpDomain,
        form: TptpFormulaForm,
        problemNumber: Long = 1,
        version: Long = 0,
        size: Long = -1
    ): Path =
        FILE_SYSTEM.getPath(
            TptpProperties.getBaseFolderName(),
            "Problems", domain.toString(),
            domain.toString() +
                    padToThreeDigits(problemNumber) +
                    form.representation +
                    version +
                    (if (size >= 0)
                        "." + padToThreeDigits(size)
                    else
                        "") +
                    ".p"
        )

    fun findAxiomsFile(
        domain: TptpDomain,
        form: TptpFormulaForm,
        axiomatizationNumber: Long = 1,
        version: Int = 0
    ): Path =
        if (domain === TptpDomain.SET && axiomatizationNumber == 7L) {
            FILE_SYSTEM.getPath(
                TptpProperties.getBaseFolderName(),
                "Axioms",
                domain.toString() + padToThreeDigits(axiomatizationNumber),
                "${domain}${padToThreeDigits(
                    axiomatizationNumber
                )}${form.representation}$version.ax"
            )
        } else
            FILE_SYSTEM.getPath(
                TptpProperties.getBaseFolderName(),
                "Axioms",
                "${domain}${padToThreeDigits(
                    axiomatizationNumber
                )}${form.representation}$version.ax"
            )

}

