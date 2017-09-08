package com.benjishults.bitnots.parser

import org.junit.Test
import java.nio.file.Path
import java.nio.file.Files
import java.nio.file.FileSystem
import java.nio.file.FileSystems

class PropLangParseTest {
    val propLangParser = PropositionalLanguageParser()
    @Test
    fun propLangParseTest() {
        println(
                propLangParser.parse(FileSystems.getDefault().getPath("src/test/resources/propositional/propositional.yaml").toFile().inputStream())
        )
    }

}