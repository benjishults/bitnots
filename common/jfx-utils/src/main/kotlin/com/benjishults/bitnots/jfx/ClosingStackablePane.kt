package com.benjishults.bitnots.jfx

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox

abstract class ClosingStackablePane(
        override val stack: CloseableStackPane
) : BorderPane(), StackablePane {

    abstract val onApply: () -> Unit

    init {
        bottom = HBox(
                Button("Back").also {
                    it.onAction = EventHandler {
                        popMe()
                    }
                    it.tooltip = Tooltip("Go back to previous pane without applying changes")
                },
                Button("Apply").also {
                    it.onAction = EventHandler {
                        onApply()
                    }
                    it.tooltip = Tooltip("Apply changes")
                },
                Button("Done").also {
                    it.onAction = EventHandler {
                        onApply()
                        stack.onClose()
                    }
                    it.tooltip = Tooltip("Apply changes and close popup")
                },
                Button("Cancel").also {
                    it.onAction = EventHandler {
                        stack.onClose()
                    }
                    it.tooltip = Tooltip("Close popup without applying changes")
                }
        ).also {
            it.alignment = Pos.CENTER_RIGHT
        }
    }


    fun addContent(node: Node) {
        center = node
    }

}
