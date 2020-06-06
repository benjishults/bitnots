package com.benjishults.bitnots.tptp.files

import com.benjishults.bitnots.parser.FileDescriptor
import com.benjishults.bitnots.tptp.TptpFileRepo
import java.util.regex.Pattern

data class TptpProblemFileDescriptor(
    val domain: TptpDomain,
    override val form: TptpFormulaForm = TptpFof,
    val number: Long = 0L,
    val version: Long = 1L,
    val size: Long = -1L
) : FileDescriptor<TptpFormulaForm, TptpFileRepo> {

    override val source: TptpFileRepo = TptpFileRepo

    companion object {
        val pattern = Pattern.compile(
            "(?<domain>[A-Z]{3})(?<number>[0-9]{3})(?<form>[-+^=_])(?<version>[1-9][0-9]*)(?<size>\\.[0-9]+)?\\.p"
        )

        val comparator = object : Comparator<TptpProblemFileDescriptor> {
            override fun compare(o1: TptpProblemFileDescriptor, o2: TptpProblemFileDescriptor) =
                o1.domain.compareTo(o2.domain).takeIf { it != 0 }
                    ?: o1.number.compareTo(o2.number).takeIf { it != 0 }
                    ?: o1.version.compareTo(o2.version).takeIf { it != 0 }
                    ?: o1.size.compareTo(o2.size)
        }

        fun fromFileName(fileName: String): TptpProblemFileDescriptor =
            pattern.matcher(fileName).let { matcher ->
                if (matcher.find()) {
                    TptpDomain.valueOf(matcher.group("domain")).let { domain ->
                        matcher.group("number").toLong(10).let { number ->
                            TptpFormulaForm.findByRepresentation(
                                matcher.group("form")[0]
                            ).let { form ->
                                matcher.group("version").toLong(10).let { version ->
                                    TptpProblemFileDescriptor(
                                        domain,
                                        form,
                                        number,
                                        version,
                                        matcher.group("size")?.substring(1)?.toLong(10) ?: -1
                                    )
                                }
                            }
                        }
                    }
                } else {
                    error("$fileName is not a properly-formatted TPTP problem file name")
                }
            }
    }

    override fun toFileName() =
        buildString {
            append(domain.name)
            append(padToThreeDigits(number))
            append(form.representation)
            append(version)
            append(
                if (size >= 0)
                    "." + padToThreeDigits(size)
                else
                    ""
            )
            append(".p")
        }

    override fun extraPropertiesToYamlString(indentSpaces: Int): String {
        val indentation = " ".repeat(indentSpaces)
        return         buildString {
            append(indentation)
            appendln("domain: ${domain}")
            append(indentation)
            appendln("size: ${size}")
            append(indentation)
            appendln("number: ${number}")
            append(indentation)
            appendln("version: ${version}")
        }

    }

}
