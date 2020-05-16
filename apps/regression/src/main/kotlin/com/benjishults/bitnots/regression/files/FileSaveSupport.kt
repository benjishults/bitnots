package com.benjishults.bitnots.regression.files

import com.benjishults.bitnots.regression.problem.ProblemFileSet
import com.benjishults.bitnots.regression.problemSetsFolder
import com.benjishults.bitnots.util.identity.id
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
        out.write("name: $name")
        out.newLine()
        out.write("problems:")
        out.newLine()
        this.problemFiles.forEach { row ->
            out.write("  - form: ${row.fileDescriptor.form.abbreviation}")
            out.newLine()
            out.write("    source: ${row.fileDescriptor.source.abbreviation}")
            out.newLine()
            out.write("    sourceVersion: ${row.fileDescriptor.source.version}")
            out.newLine()
            out.write("    finishingStrategy: ${row.harness.prover.finishingStrategy.id()}")
            out.newLine()
            out.write("    finishingStrategyVersion: ${row.harness.prover.finishingStrategy.version}")
            out.newLine()
            out.write("    stepStrategy: ${row.harness.prover.stepStrategy.id()}")
            out.newLine()
            out.write("    stepStrategyVersion: ${row.harness.prover.stepStrategy.version}")
            out.newLine()
            out.write("    prover: ${row.harness.prover.id()}")
            out.newLine()
            out.write("    proverVersion: ${row.harness.prover.version}")
            out.newLine()
            out.write("    harness: ${row.harness.id()}")
            out.newLine()
            out.write("    harnessVersion: ${row.harness.version}")
            out.newLine()
        }
    }

}
