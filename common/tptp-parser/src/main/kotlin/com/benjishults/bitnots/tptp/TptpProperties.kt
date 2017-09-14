package com.benjishults.bitnots.tptp

import java.io.File
import java.nio.file.FileSystems
import java.util.Properties

object TptpProperties : Properties() {
    init {
        load(File("src/main/resources/tptp.properties").reader().buffered())
    }

    fun getBaseFolderName() = get("tptp.base.folder")
}