package com.benjishults.bitnots.tptp

import java.io.File
import java.util.Properties

/**
 * If you use this class, the 'tptpHome' system property or the 'TPTP_HOME' environment variable must be set to the root of the TPTP distro.
 */
object TptpProperties : Properties() {

    init {
        val tptpHome = System.getProperty("tptpHome") ?: System.getenv("TPTP_HOME")
        load(File(tptpHome + File.pathSeparator + "tptp.properties").reader().buffered())
    }

    fun getBaseFolderName() = get("tptp.base.folder")
}