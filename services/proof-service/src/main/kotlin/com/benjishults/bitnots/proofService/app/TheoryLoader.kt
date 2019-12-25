package com.benjishults.bitnots.proofService.app

import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.io.File

class TheoryLoader : View("Load a Problem") {
    val selectedFile = SimpleStringProperty("file")
    val fileContents = SimpleStringProperty()
    lateinit var file: File

    init {
        file = chooseFile("Select", arrayOf()) {
            initialDirectory = File(System.getProperty("user.home"))
        }[0]
        selectedFile.value = file.absolutePath
        fileContents.value = file.inputStream().use {
            String(it.readAllBytes())
        }
    }

    override val root = vbox {
        label(selectedFile)
        textarea(fileContents)
    }
}
