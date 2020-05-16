package com.benjishults.bitnots.util.identity

import java.time.Instant
import java.util.*

interface Versioned {
    val version: String
}

object CommitIdTimeVersioner : Versioned {

    val now = Instant.now()

    override val version = "${gitCommitTime()}__${gitCommitId()}"

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
