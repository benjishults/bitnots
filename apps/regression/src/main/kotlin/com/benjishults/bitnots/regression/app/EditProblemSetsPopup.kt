package com.benjishults.bitnots.regression.app

// import com.benjishults.bitnots.parser.ProblemSet
import javafx.stage.Stage
import javafx.stage.Window


class EditProblemSetsPopup(owner: Window) : Stage() { // }, vararg items: ProblemSet) : Stage() {

    // private val tableView = TableView<ProblemSet>()
    //
    // init {
    //
    //     initOwner(owner)
    //     initModality(Modality.APPLICATION_MODAL)
    //     initStyle(StageStyle.DECORATED)
    //     title = "Edit Problem Sets"
    //
    //     scene = Scene(SplitPane().also { splitPane ->
    //         val rightPane = Pane()
    //         splitPane.styleClass.add("ux")
    //         splitPane.items.addAll(
    //                 VBox(
    //                         HBox(
    //                                 Button("New").also { newButton ->
    //                                     newButton.onAction = EventHandler {
    //                                         // rightPane.children.add(
    //                                                 // NewProblemSetDialog(tableView.items, rightPane))
    //                                     }
    //                                 },
    //                                 Button("Delete").also { delButton ->
    //                                     delButton.onAction = EventHandler {
    //                                         ChoiceDialog<String>().also { choiceDialog ->
    //                                             choiceDialog.items.addAll("Delete", "Cancel")
    //                                             choiceDialog.showAndWait().ifPresent { choice ->
    //                                                 choice.takeIf {
    //                                                     it == "Delete"
    //                                                 }.also {
    //                                                     tableView.items.removeAt(
    //                                                             tableView.selectionModel.selectedIndex)
    //                                                 }
    //                                             }
    //                                         }
    //                                     }
    //                                 }),
    //                         ScrollPane(tableView)),
    //                 rightPane)
    //         return@also
    //     })
    //     scene.stylesheets.add("css/ui.css")
    //
    //     tableView.selectionModel.selectionMode = SelectionMode.SINGLE
    //     tableView.items.addAll(*items)
    //     tableView.sortOrder
    //     tableView.columns.addAll(
    //             TableColumn<ProblemSet, String>("Name").also {
    //                 it.cellValueFactory = Callback { param ->
    //                     SimpleStringProperty(param.value.name)
    //                 }
    //             },
    //             TableColumn<ProblemSet, String>("Type").also {
    //                 it.cellValueFactory = Callback { param ->
    //                     SimpleStringProperty(param.value.type.toString())
    //                 }
    //             })
    //
    // }

}
