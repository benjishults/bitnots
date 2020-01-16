package com.benjishults.bitnots.tptp.files

import com.benjishults.bitnots.tptp.TptpProperties
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

object TptpFileFetcher {

    // A regular expression for recognizing axiom file names is "[A-Z]{3}[0-9]{3}[-+^=_][1-9][0-9]*\.ax".

    val FILE_SYSTEM = FileSystems.getDefault()

    private fun padToThreeDigits(version: Int) =
            String.format("%03d", version)

    fun findAll(domain: TptpDomain, form: TptpFormulaForm): List<Path> {
        val pattern = Pattern.compile("${domain}[0-9]{3}${form.form.takeIf {
            it != '+'
        }?.toString() ?: "\\+"}[1-9][0-9]*(?:\\.[0-9]{3})?\\.p")
        return Files.newDirectoryStream(
                FILE_SYSTEM.getPath(
                        TptpProperties.getBaseFolderName() as String,
                        "Problems", domain.toString()
                )
        ).filter {
            pattern.matcher(it.getFileName().toString()).matches()
        }.toList()
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

