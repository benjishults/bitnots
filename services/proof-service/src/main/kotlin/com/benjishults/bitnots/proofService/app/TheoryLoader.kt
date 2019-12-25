package com.benjishults.bitnots.proofService.app

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class TheoryLoader : View("Load a Problem") {
    val selectedFile = SimpleStringProperty("file").apply { value = "first" }
    override val root = vbox {
        combobox(selectedFile, listOf("first", "second"))
        button("Load") {
            action {
                openInternalWindow(object : View("Selected") {
                    override val root = label("selected: ${selectedFile.value}")

                })
            }
        }
    }
}
