package com.benjishults.bitnots.tptp.parser

import org.junit.Test
import java.nio.file.FileSystems

class CnfParserTest {

    private val path = FileSystems.getDefault().getPath("src/test/resources/PLA001-1.p")

    @Test
    fun parserTest() {
        path.toFile().bufferedReader().use {
            try {
                TptpParser.parseFile(path).let {
                    println(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}