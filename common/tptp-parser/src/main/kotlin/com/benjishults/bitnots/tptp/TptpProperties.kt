package com.benjishults.bitnots.tptp

import java.io.File
import java.util.*

/**
 * If you use this class, the 'tptpHome' system property or the 'TPTP_HOME' environment variable must be set to the root of the TPTP distro.
 */
object TptpProperties : Properties() {

    init {
        val config = System.getProperty("config") ?: "src/main/resources"
        load(File(config + File.separator + "tptp.properties").reader().buffered())
    }

    fun getBaseFolderName() =
            get("tptp.base.folder") as String

    fun getWriteResultsFolderName() =
            get("tptp.write.results.folder") as String

    fun getReadResultsFolderName() =
            get("tptp.read.results.folder") as String
}
