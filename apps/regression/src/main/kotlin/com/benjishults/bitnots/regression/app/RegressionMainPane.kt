package com.benjishults.bitnots.regression.app

import com.benjishults.bitnots.parser.FormulaForm
import com.benjishults.bitnots.parser.ProblemSource
import com.benjishults.bitnots.regression.app.problem.ProblemRow
import com.benjishults.bitnots.tableauProver.FolTableauHarness
import com.benjishults.bitnots.theory.DomainCategory
import com.benjishults.bitnots.tptp.TptpFileRepo
import com.benjishults.bitnots.tptp.files.TptpProblemFileDescriptor
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

class RegressionMainPane(
        private val uiProperties: Properties,
        width: Double,
        height: Double,
        menuBar: MenuBar = MenuBar(),
        pane: BorderPane = BorderPane()
) : Scene(VBox(menuBar, pane), width, height) {

    private val problemSetName = SimpleStringProperty("No problem set selected")

    // private val textArea = TextArea()
    // private lateinit var proofContext: Problem
    private val table: TableView<ProblemRow>

    init {
        table = problemTable()
        stylesheets.add("css/ui.css")

        val fileMenu = Menu("File")
        // val editMenuItem = MenuItem("Edit Problem Set Collection")
        // editMenuItem.onAction = EventHandler<ActionEvent> {
        //     EditProblemSetsPopup(window).show()
        // }

        fileMenu.items.addAll(
                newProblemSetMenuItem(),
                openProblemSetMenuItem(),
                recentProblemSetsMenu())

        menuBar.menus.addAll(fileMenu, Menu("Help"))

        pane.idProperty().set("root")
        pane.top = Label().apply {
            textProperty().bind(problemSetName)
        }
        pane.center = ScrollPane(table)
        pane.bottom = FlowPane().also { bottomPane ->
            bottomPane.children.addAll(
                    Button("Run").also { runButton ->
                        runButton.setOnAction {
                            TODO("Implement")
                        }
                    },
                    Button("See History").apply {
                        setOnAction { _ ->
                            TODO("Implement")
                            // loadFile("problem")?.let {
                            //     // TODO set as current problem
                            //     loadProblem(it)
                            // }
                        }
                    },
                    Button("Unlock").apply {
                        setOnAction { _ ->
                            TODO("Implement")
                            // loadFile("theory")?.let {
                            //     // TODO add to theories loaded
                            // }
                        }
                    },
                    Button("Delete Problem Set").apply {
                        setOnAction { _ ->
                            TODO("Implement")
                        }
                    })
        }
    }

    private fun problemTable(): TableView<ProblemRow> {
        val table = TableView<ProblemRow>()
        val fileNameCol = TableColumn<ProblemRow, String>("File")
        fileNameCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.fileName)
        }
        val sourceCol = TableColumn<ProblemRow, ProblemSource>("Source")
        sourceCol.setCellValueFactory { _ ->
            ReadOnlyObjectWrapper(TptpFileRepo)
        }
        val domainCol = TableColumn<ProblemRow, DomainCategory>("Domain")
        domainCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.descriptor.domain)
        }
        val formCol = TableColumn<ProblemRow, FormulaForm>("Form")
        formCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.descriptor.form)
        }
        val qLimitCol = TableColumn<ProblemRow, Long?>("Q-Limit")
        qLimitCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.harness.qLimit)
        }
        val stepLimitCol = TableColumn<ProblemRow, Long?>("Step Limit")
        stepLimitCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(column.value.harness.stepLimit)
        }
        val timeLimitCol = TableColumn<ProblemRow, BigDecimal?>("Time Limit (s)")
        timeLimitCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(
                    column.value.harness.timeLimitMillis?.div(1000.0)
                        ?.let { num ->
                            BigDecimal(num, MathContext(2, RoundingMode.HALF_UP))
                        })
        }
        val lastTimeCol = TableColumn<ProblemRow, Long?>("Last Time (ms)")
        lastTimeCol.setCellValueFactory { column ->
            ReadOnlyObjectWrapper(null)
        }
        table.columns.addAll(
                fileNameCol,
                sourceCol,
                domainCol,
                formCol,
                qLimitCol,
                stepLimitCol,
                timeLimitCol,
                lastTimeCol)
        return table
    }

    private fun recentProblemSetsMenu() = Menu("Recent")

    private fun openProblemSetMenuItem() = MenuItem("Open...")

    private fun newProblemSetMenuItem(): MenuItem {
        return MenuItem("New").also { new ->
            new.setOnAction { e: ActionEvent ->
                NewProblemSetDialog().showAndWait().ifPresentOrElse(
                        { builder ->
                            // Popup().also { popup ->
                            //     popup.content.add(Text("Initializing Problem Set '${builder.name}'"))
                            //     popup.show(this.window)
                            table.items = FXCollections.observableList(builder.build().problems.map { descriptor ->
                                if (descriptor is TptpProblemFileDescriptor) {
                                    ProblemRow(descriptor.toFileName(), descriptor, FolTableauHarness(3, null, null),
                                               -1L)
                                } else error("not TPTP")
                            }.toList())
                            // popup.hide()
                            // }
                        }
                ) {
                    // do nothing
                }
            }

        }
    }

    // private fun loadProblem(path: Path) {
    //     val axioms = mutableListOf<FolAnnotatedFormula>()
    //     val conjectures = mutableListOf<FolAnnotatedFormula>()
    //     // TODO create a single theory and a problem for each conjecture
    //     TptpFofParser.parseFile(path).forEach { annotatedFormula ->
    //         when (annotatedFormula.formulaRole) {
    //             FormulaRole.conjecture -> {
    //                 conjectures.add(annotatedFormula)
    //             }
    //             FormulaRole.assumption,
    //             FormulaRole.axiom,
    //             FormulaRole.definition,
    //             FormulaRole.hypothesis -> {
    //                 axioms.add(annotatedFormula)
    //             }
    //             else                   -> {
    //                 // ignore for now
    //             }
    //         }
    //     }
    //     // proofContext = Problem(axioms, conjectures)
    // }

    // private fun loadTheory() {
    //
    // }

    // private fun loadFile(category: String): Path? {
    //     // TODO also save
    //     val folderKey = "ui.${inputFormat.get()}${category.capitalize()}InputFolder"
    //     return FileChooser().run {
    //         initialDirectory = File(uiProperties.getOrDefault(folderKey, System.getProperty("user.home")) as String)
    //         title = "Select ${category} file"
    //         showOpenDialog(this@RegressionMainPane.window)
    //     }?.run {
    //         uiProperties.put(folderKey, toPath().parent.toString())
    //         problemSetName.value = absolutePath
    //         textArea.text = inputStream().use {
    //             String(it.readBytes())
    //         }
    //         toPath()
    //     }
    // }

}
