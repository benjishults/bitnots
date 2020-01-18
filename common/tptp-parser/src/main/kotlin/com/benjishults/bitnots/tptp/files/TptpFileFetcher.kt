package com.benjishults.bitnots.tptp.files

import com.benjishults.bitnots.tptp.TptpProperties
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.regex.Pattern

fun padToThreeDigits(version: Int) =
        String.format("%03d", version)

object TptpFileFetcher {

    // A regular expression for recognizing axiom file names is "[A-Z]{3}[0-9]{3}[-+^=_][1-9][0-9]*\.ax".

    val FILE_SYSTEM = FileSystems.getDefault()

    fun findProblemFolder(domain: TptpDomain) = FILE_SYSTEM.getPath(
            TptpProperties.getBaseFolderName() as String,
            "Problems", domain.toString())

    fun findAll(domain: TptpDomain, form: TptpFormulaForm): List<Path> {
        val pattern = Pattern.compile("${domain}[0-9]{3}${form.form.takeIf {
            it != '+'
        }?.toString() ?: "\\+"}[1-9][0-9]*(?:\\.[0-9]{3})?\\.p")
        return Files.newDirectoryStream(findProblemFolder(domain)).filter {
            pattern.matcher(it.getFileName().toString()).matches()
        }.toList()
    }

    fun findFile(descriptor: TptpProblemFileDescriptor): List<Path> {
        val domain = descriptor.domain
        val form = descriptor.form
        val pattern = Pattern.compile("${domain}[0-9]{3}${form.form.takeIf {
            it != '+'
        }?.toString() ?: "\\+"}[1-9][0-9]*(?:\\.[0-9]{3})?\\.p")
        return Files.newDirectoryStream(findProblemFolder(domain)).filter {
            pattern.matcher(it.getFileName().toString()).matches()
        }.toList()
    }

    fun problemFileFilter(domains: List<TptpDomain>, forms: List<TptpFormulaForm>, vararg excludes: TptpProblemFileDescriptor): List<TptpProblemFileDescriptor> {
        val base = FILE_SYSTEM.getPath(
                TptpProperties.getBaseFolderName() as String,
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
                    if (descriptor.domain === domain && forms.contains(descriptor.form) && !excludes.contains(descriptor))
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
                    TptpProperties.getBaseFolderName() as String,
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
                        TptpProperties.getBaseFolderName() as String,
                        "Axioms",
                        domain.toString() + padToThreeDigits(axiomatizationNumber),
                        "${domain.toString()}${padToThreeDigits(
                                axiomatizationNumber)}${form.form}$version.ax"
                )
            } else
                FILE_SYSTEM.getPath(
                        TptpProperties.getBaseFolderName() as String,
                        "Axioms",
                        "${domain.toString()}${padToThreeDigits(
                                axiomatizationNumber)}${form.form}$version.ax"
                )

}

