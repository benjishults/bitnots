package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.jfx.CloseableStackPane
import com.benjishults.bitnots.parser.ProblemSet
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ChoiceDialog
import javafx.scene.control.ScrollPane
import javafx.scene.control.SelectionMode
import javafx.scene.control.SplitPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import javafx.util.Callback


class EditProblemSetsPopup(owner: Window, vararg items: ProblemSet) : Stage() {

    val stackPane: CloseableStackPane = object : CloseableStackPane() {
        override val onClose: () -> Unit = {
            this@EditProblemSetsPopup.onCloseRequest.handle(null)
        }

    }
    private val tableView = TableView<ProblemSet>()

    init {

        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)
        initStyle(StageStyle.DECORATED)
        title = "Edit Problem Sets"

        scene = Scene(SplitPane().also { splitPane ->
            splitPane.styleClass.add("ux")
            splitPane.items.addAll(
                    VBox(
                            HBox(
                                    Button("New").also { newButton ->
                                        newButton.onAction = EventHandler {
                                            stackPane.children.add(
                                                    NewProblemSetPane(tableView.items, stackPane))
                                        }
                                    },
                                    Button("Delete").also { delButton ->
                                        delButton.onAction = EventHandler {
                                            ChoiceDialog<String>().also { choiceDialog ->
                                                choiceDialog.items.addAll("Delete", "Cancel")
                                                choiceDialog.showAndWait().ifPresent { choice ->
                                                    choice.takeIf {
                                                        it == "Delete"
                                                    }.also {
                                                        tableView.items.removeAt(
                                                                tableView.selectionModel.selectedIndex)
                                                    }
                                                }
                                            }
                                        }
                                    }),
                            ScrollPane(tableView)),
                    stackPane)
            return@also
        })
        scene.stylesheets.add("css/ui.css")

        tableView.selectionModel.selectionMode = SelectionMode.SINGLE
        tableView.items.addAll(*items)
        tableView.sortOrder
        tableView.columns.addAll(
                TableColumn<ProblemSet, String>("Name").also {
                    it.cellValueFactory = Callback { param ->
                        SimpleStringProperty(param.value.name)
                    }
                },
                TableColumn<ProblemSet, String>("Type").also {
                    it.cellValueFactory = Callback { param ->
                        SimpleStringProperty(param.value.type.toString())
                    }
                })

    }

}
