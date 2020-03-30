package com.benjishults.bitnots.tptp

import com.benjishults.bitnots.parser.ProblemSource
import java.io.File
import java.nio.file.Path
import java.util.*

/**
 * If you use this class, the 'config' system property must be set to the folder containing a file named
 * `tptp.properties` containing definitions for the following properties:
 *
 * * `tptp.base.folder`
 * * `tptp.write.results.folder`
 * * `tptp.read.results.folder`
 */
object TptpProperties : Properties() {

    init {
        val config = System.getProperty("config") ?: "src/main/resources"
        load(File(config + File.separator + "tptp.properties").reader().buffered())
    }

    fun getBaseFolderName() =
            get("tptp.base.folder") as String

    fun getTptpVersion() =
            get("tptp.version") as String

    fun getWriteResultsFolderName() =
            get("tptp.write.results.folder") as String

    fun getReadResultsFolderName() =
            get("tptp.read.results.folder") as String
}

object TptpFileRepo : ProblemSource() {
    val version: String = TptpProperties.getTptpVersion()
    val path: Path = Path.of(TptpProperties.getBaseFolderName())
    override fun toString() = "TPTP"
}
