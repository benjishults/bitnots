package com.benjishults.bitnots.regression

import com.benjishults.bitnots.regression.app.FxApplication
import javafx.application.Application
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    Files.createDirectories(userConfigFolder)
    Files.createDirectories(problemSetsFolder)
    Application.launch(FxApplication::class.java, *args)
}

val userConfigFolder = Paths.get(System.getProperty("user.home"), ".bitnots")
val problemSetsFolder = userConfigFolder.resolve("problemSets")
