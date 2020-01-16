package com.benjishults.bitnots.tptp.files

import java.nio.file.Path
import java.util.regex.Pattern

data class TptpProblemFileDescriptor(val domain: TptpDomain, val form: TptpFormulaForm = TptpFormulaForm.FOF,
                                     val number: Int = 0, val version: Int = 1, val size: Int = -1) {

    companion object {
        val pattern = Pattern.compile(
                "(?<domain>[A-Z]{3})(?<number>[0-9]{3})(?<form>[-+^=_])(?<version>[1-9][0-9]*)(?<size>\\.[0-9]+)?\\.p")

        fun parseTptpPath(path: Path): TptpProblemFileDescriptor =
                pattern.matcher(path.fileName.toString()).let { matcher ->
                    if (matcher.find()) {
                        TptpDomain.valueOf(matcher.group("domain")).let { domain ->
                            matcher.group("number").toInt(10).let { number ->
                                TptpFormulaForm.findByForm(
                                        matcher.group("form")[0]).let { form ->
                                    matcher.group("version").toInt(10).let { version ->
                                        TptpProblemFileDescriptor(
                                                domain,
                                                form,
                                                number,
                                                version,
                                                matcher.group("size")?.substring(1)?.toInt(10) ?: -1
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        error("$path is not a properly-formatted TPTP problem file name")
                    }
                }
    }
}
