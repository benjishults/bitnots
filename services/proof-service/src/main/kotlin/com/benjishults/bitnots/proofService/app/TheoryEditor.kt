package com.benjishults.bitnots.proofService.app

import com.benjishults.bitnots.proofService.app.config.UiConfig
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import java.io.File
import java.nio.charset.Charset

class TheoryEditor(val uiConfig: UiConfig, val file: File) :
        Scene(VBox().apply {
            alignment = Pos.CENTER
            spacing = 20.0
            padding = Insets(25.0, 25.0, 25.0, 25.0)

            with(children) {
                add(TextArea(file.inputStream().run {
                    String(readAllBytes(), Charset.forName("UTF8"))
                }).apply {
                    font = Font.font("mono", uiConfig.monoFontSize)
                    isWrapText = false
                })
                add(FlowPane(10.0, 10.0).apply {
                    with(children) {
                        add(ComboBox(FXCollections.unmodifiableObservableList(FXCollections.observableList(listOf("tptp", "ipr", "bitnots")))))
                        add(Button("Launch Prover"))
                        add(Button("Save"))
                    }
                })
            }
        }) {

}
