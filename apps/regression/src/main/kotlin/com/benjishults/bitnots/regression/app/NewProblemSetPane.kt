package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.parser.ProblemSet
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane

class NewProblemSetPane(items: ObservableList<ProblemSet>, parent: Pane) : BorderPane() {

    init {
        bottom = FlowPane(
                Button("Add").also {
                    it.onAction = EventHandler {
                        // TODO add new Problem Set
                        parent.children.clear()
                    }
                    it.tooltip = Tooltip("Add this problem set to your collection")
                },
                Button("Cancel").also {
                    parent.children.clear()
                }
        ).also {
            it.alignment = Pos.BOTTOM_RIGHT
        }
top =         FlowPane().also { bottomPane ->
    bottomPane.children.add(Label("format", ComboBox(FXCollections.unmodifiableObservableList(
            FXCollections.observableList(inputFormats))).apply {
        inputFormat.bind(valueProperty())
        value = uiProperties.getProperty("ui.chosenInputFormat", inputFormats.first())
        setOnAction {
            uiProperties.put("ui.chosenInputFormat", value)
        }
    }))

}

}
