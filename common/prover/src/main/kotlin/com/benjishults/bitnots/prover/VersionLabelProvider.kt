package com.benjishults.bitnots.prover

import java.time.Instant
import java.util.*

interface VersionLabelProvider {
    fun versionLabel(): String
}

object TimeCommitIdVersionLabelProvider : VersionLabelProvider {

    val now = Instant.now()

    override fun versionLabel() = "${gitCommitTime()}__${gitCommitId()}"

    private fun gitCommitId() =
        gitCommitInfo()["git.commit.id"]?.let { it as String } ?: "dirty-SNAPSHOT"

    private fun gitCommitTime(): Instant? =
        gitCommitInfo()["git.commit.time"]?.let { Instant.parse(it as String) } ?: now

    private fun gitCommitInfo() =
        javaClass.classLoader.getResource("git.properties")?.let { url ->
            url.file
                .reader()
                .buffered()
                .use { reader ->
                    Properties().also {
                        it.load(reader)
                    }
                }
        } ?: Properties()

}
