package com.benjishults.bitnots.proofService.app

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.FileChooser
import org.apache.camel.CamelContext
import java.io.File

class TheoryEditor(val context: CamelContext, width: Double, height: Double) : Scene(BorderPane(), width, height) {

    private val fileName = SimpleStringProperty("no file loaded")
    private val fileType = SimpleStringProperty("tptp")
    private val textArea = TextArea()

    init {
        stylesheets.add("css/ui.css")
        with(root as BorderPane) {
            idProperty().set("root")
            top = Label().apply {
                textProperty().bind(fileName)
            }
            center = ScrollPane(textArea)
            bottom = FlowPane().apply {
                with(children) {
                    add(Label("type", ComboBox(FXCollections.unmodifiableObservableList(FXCollections.observableList(listOf("tptp", "ipr", "bitnots")))).apply {
                        fileType.bind(valueProperty())
                        value = "tptp"
                    }))
                    add(Button("Load Problem").apply {
                        setOnAction { _ ->
                            loadProblemFile()
                        }
                    })
                    add(Button("Load Theory").apply {
                        setOnAction { _ ->
                            loadTheoryFile()
                        }
                    })
                    add(Button("Launch Prover"))
                    add(Button("Save"))
                }
            }
        }
    }

    private fun loadProblemFile() {
        val file = FileChooser().run {
            initialDirectory = File(context.propertiesComponent.resolveProperty("ui.baseInputFolder").orElse(null))
            title = "Select problem file"
            showOpenDialog(this@TheoryEditor.window)
        }
        fileName.value = file.absolutePath
        textArea.text = file.inputStream().use {
            String(it.readAllBytes())
        }
    }

    private fun loadTheoryFile() {
        val file = FileChooser().run {
            initialDirectory = File(context.propertiesComponent.resolveProperty("ui.baseInputFolder").orElse(null))
            title = "Select theory file"
            showOpenDialog(this@TheoryEditor.window)
        }
        fileName.value = file.absolutePath
        textArea.text = file.inputStream().use {
            String(it.readAllBytes())
        }
    }

}
