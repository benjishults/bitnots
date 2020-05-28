package com.benjishults.bitnots.regression.files

import com.benjishults.bitnots.regression.problem.ProblemFileSet
import com.benjishults.bitnots.regression.problemSetsFolder
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

fun String.willOverwriteExistingProblemSet(): Boolean {
    return problemSetsFolder.contains(Path.of(this))
}

fun ProblemFileSet<*>.writeNewProblemSet() {
    val problemSetFolder = problemSetsFolder.resolve(this.name)
    Files.createDirectories(problemSetFolder.resolve("results"))
    val problemSetFile = problemSetFolder.resolve("problemSet.yml")
    if (Files.exists(problemSetFile)) {
        Files.walkFileTree(problemSetFolder, object : SimpleFileVisitor<Path>() {
            override fun visitFile(path: Path, attrs: BasicFileAttributes?): FileVisitResult {
                path.toFile().deleteRecursively()
                return FileVisitResult.CONTINUE
            }
        })
        Files.createDirectories(problemSetFolder.resolve("results"))
    }
    Files.createFile(problemSetFile)
    problemSetFile.toFile().bufferedWriter().use { out ->
        out.write(
            """
name: $name
problems:
"""
        )
        this.problemFiles.forEach { row ->
            out.write(
                """
  - form: ${row.fileDescriptor.form.abbreviation}
    source: ${row.fileDescriptor.source.abbreviation}
    sourceVersion: ${row.fileDescriptor.source.version}
    finishingStrategy: ${row.harness.prover.finishingStrategy.id}
    finishingStrategyVersion: ${row.harness.prover.finishingStrategy.version}
    stepStrategy: ${row.harness.prover.stepStrategy.id}
    stepStrategyVersion: ${row.harness.prover.stepStrategy.version}
    prover: ${row.harness.prover.id}
    proverVersion: ${row.harness.prover.version}
    harness: ${row.harness.id}
    harnessVersion: ${row.harness.version}
            """
            )
        }
    }

}
