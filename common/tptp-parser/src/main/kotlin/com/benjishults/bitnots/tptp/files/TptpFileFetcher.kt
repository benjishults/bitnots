package com.benjishults.bitnots.tptp.files

import com.benjishults.bitnots.parser.FileFetcher
import com.benjishults.bitnots.tptp.TptpProperties
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.regex.Matcher
import java.util.regex.Pattern

fun padToThreeDigits(version: Int) =
        String.format("%03d", version)

object TptpFileFetcher : FileFetcher<TptpDomain, TptpFormulaForm, TptpProblemFileDescriptor> {

    // A regular expression for recognizing axiom file names is "[A-Z]{3}[0-9]{3}[-+^=_][1-9][0-9]*\.ax".

    private val FILE_SYSTEM: FileSystem = FileSystems.getDefault()

    override fun findProblemFolder(domain: TptpDomain): Path = FILE_SYSTEM.getPath(
            TptpProperties.getBaseFolderName(),
            "Problems", domain.toString())

    override fun findAllPaths(
            domain: TptpDomain,
            form: TptpFormulaForm
    ): List<Path> {
        val pattern = problemPattern(domain, form)
        return Files.newDirectoryStream(findProblemFolder(domain)).filter {
            pattern.matcher(it.fileName.toString()).matches()
        }.toList()
    }

    override fun findAllDescriptors(
            domain: TptpDomain,
            form: TptpFormulaForm
    ): List<TptpProblemFileDescriptor> {
        val pattern = problemPattern(domain, form)
        return Files.newDirectoryStream(findProblemFolder(domain))
            .map { path ->
                pattern.matcher(path.fileName.toString())
            }
            .filter(Matcher::matches)
            .map { matcher ->
                TptpProblemFileDescriptor(
                        domain,
                        form,
                        matcher.group("number").toInt(10),
                        matcher.group("version").toInt(10),
                        matcher.group("size")?.toInt(10) ?: -1)
            }
            .toList()
    }

    private fun problemPattern(
            domain: TptpDomain,
            form: TptpFormulaForm
    ): Pattern {
        return Pattern.compile("${domain}(?<number>[0-9]{3})${form.form.takeIf {
            it != '+'
        }?.toString() ?: "\\+"}(?<version>[1-9][0-9]*)(?:\\.(?<size>[0-9]{3}?))\\.p")
    }

    override fun findProblemFile(descriptor: TptpProblemFileDescriptor): Path {
        return findProblemFolder(descriptor.domain).resolve(descriptor.toFileName())
    }

    override fun problemFileFilter(
            domains: List<TptpDomain>,
            forms: List<TptpFormulaForm>,
            vararg excludes: TptpProblemFileDescriptor
    ): List<TptpProblemFileDescriptor> {
        val base = FILE_SYSTEM.getPath(
                TptpProperties.getBaseFolderName(),
                "Problems")
        val value = mutableListOf<TptpProblemFileDescriptor>()
        domains.forEach { domain ->
            val domainPath = base.resolve(domain.toString())
            Files.walkFileTree(domainPath, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    val descriptor =
                            try {
                                TptpProblemFileDescriptor.parseTptpPath(file)
                            } catch (e: IllegalStateException) {
                                return FileVisitResult.CONTINUE
                            }
                    if (descriptor.domain === domain && forms.contains(descriptor.form) && !excludes.contains(
                                descriptor))
                        value.add(descriptor)
                    return FileVisitResult.CONTINUE
                }
            })
        }
        return value.sortedWith(TptpProblemFileDescriptor.comparator)
    }

    fun findProblemFile(
            domain: TptpDomain,
            form: TptpFormulaForm,
            problemNumber: Int = 1,
            version: Int = 0,
            size: Int = -1
    ): Path =
            FILE_SYSTEM.getPath(
                    TptpProperties.getBaseFolderName(),
                    "Problems", domain.toString(),
                    domain.toString() +
                    padToThreeDigits(problemNumber) +
                    form.form +
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
            axiomatizationNumber: Int = 1,
            version: Int = 0
    ): Path =
            if (domain === TptpDomain.SET && axiomatizationNumber == 7) {
                FILE_SYSTEM.getPath(
                        TptpProperties.getBaseFolderName(),
                        "Axioms",
                        domain.toString() + padToThreeDigits(axiomatizationNumber),
                        "${domain}${padToThreeDigits(
                                axiomatizationNumber)}${form.form}$version.ax"
                )
            } else
                FILE_SYSTEM.getPath(
                        TptpProperties.getBaseFolderName(),
                        "Axioms",
                        "${domain}${padToThreeDigits(
                                axiomatizationNumber)}${form.form}$version.ax"
                )

}

