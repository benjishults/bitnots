package com.benjishults.bitnots.jfx

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.layout.GridPane

class FormBuilder(
        alignment: Pos = Pos.CENTER,
        hgap: Double = 10.0,
        vgap: Double = 10.0,
        padding: Insets = Insets(25.0, 25.0, 25.0, 25.0)
) {

    val form = GridPane()
    private var numberOfRows = 0

    init {
        form.alignment = Pos.CENTER;
        form.hgap = 10.0;
        form.vgap = 10.0;
        form.padding = Insets(25.0, 25.0, 25.0, 25.0);
    }

    fun addLabelAndControl(label: Label, control: Control) {
        form.add(label, 0, numberOfRows)
        form.add(control, 1, numberOfRows)
        label.labelFor = control
        numberOfRows++
    }

}
